package com.lumeneditor.www.security;

import com.lumeneditor.www.exception.CustomException;
import com.lumeneditor.www.exception.CustomExpiredJwtException;
import com.lumeneditor.www.exception.InvalidTokenException;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private final Key key;
    private static final long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_COUNT = 8 * 60 * 60 * 1000L; // 8시간
    private static final String TOKEN_TYPE = "JWT";
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final String CLAIM_ADMIN_USER_ID = "sub";
    private static final String CLAIM_IS_ADMIN = "roles";

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(RedisTemplate<String, String> redisTemplate, @Value("${jwt.secret}") String secretKey) {
        // 시크릿 키가 null 또는 빈 문자열인 경우 예외를 발생시킵니다.
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty.");
        }

        // Base64 디코딩을 사용하여 시크릿 키 문자열을 바이트 배열로 변환합니다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        this.redisTemplate = redisTemplate;

        // 변환된 바이트 배열을 사용하여 HmacSHA 키를 생성합니다.
        this.key = Keys.hmacShaKeyFor(keyBytes);

    }


    /**
     * 인증된 사용자를 위한 액세스 토큰과 리프레시 토큰을 생성합니다.
     * 이 메서드는 사용자의 인증 정보를 바탕으로 JWT 토큰을 생성하며, 사용자의 권한을 토큰에 포함시킵니다.
     * 생성된 액세스 토큰은 30분 동안 유효하며, 리프레시 토큰은 8시간 동안 유효합니다.
     * 리프레시 토큰은 Redis에 저장되어 액세스 토큰 재발급 시 사용됩니다.
     *
     * @param authentication Spring Security의 Authentication 객체, 인증된 사용자의 정보를 포함합니다.
     * @return 생성된 JWT 액세스 토큰을 포함하는 JwtToken 객체를 반환합니다.
     */

    public JwtToken generateToken(Authentication authentication) {
        String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 유효시간: 30분 (30 * 60 * 1000)
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_COUNT);
        String accessToken = Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .setSubject(authentication.getName())
                .claim(CLAIM_IS_ADMIN, roles)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SIGNATURE_ALGORITHM)
                .compact();

        // Refresh Token 유효시간: 8시간 (8 * 60 * 60 * 1000)
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_COUNT);
        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SIGNATURE_ALGORITHM)
                .compact();


        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                REFRESH_TOKEN_EXPIRE_COUNT,
                TimeUnit.MILLISECONDS
        );

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }


    /**
     * 사용자 정보를 기반으로 JWT 액세스 토큰을 생성합니다.
     * <p>
     * 이 메서드는 현재 시간을 기준으로 설정된 유효 시간을 더해 액세스 토큰의 만료 시간을 계산합니다.
     * 생성된 토큰에는 사용자 ID를 주제(subject)로, 사용자의 역할을 권한(claim)으로 포함하여 구성됩니다.
     * 이를 통해 생성된 JWT 토큰은 HTTP 요청에 포함되어 서버로 전송될 때, 사용자의 인증 및 권한 확인에 사용됩니다.
     * <p>
     * @param user 액세스 토큰을 생성하기 위한 사용자 정보가 담긴 User 객체입니다.
     * @return 생성된 JWT 액세스 토큰 문자열을 반환합니다.
     */
    public String generateAccessToken(com.lumeneditor.www.domain.auth.entity.User user) {


        // 현재 시간을 기준으로 액세스 토큰의 만료 시간을 계산
        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_COUNT);

        return Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .setSubject(user.getUserId()) // tokenData가 존재한다는 것이 검증되었으므로 get() 호출이 안전
                .claim(CLAIM_IS_ADMIN, "ROLE_" + user.getRole())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SIGNATURE_ALGORITHM)
                .compact();
    }


    /**
     * JWT 토큰에서 관리자 사용자 정보를 추출합니다.
     * <p>
     * 이 메서드는 주어진 JWT 토큰을 파싱하여 클레임을 추출하고, 클레임에서 관리자 사용자의 ID를 반환합니다.
     * 토큰이 유효하고 관리자 사용자 ID가 포함되어 있는 경우, 해당 ID를 문자열로 반환합니다.
     * 만약 토큰 파싱 과정에서 문제가 발생하거나, 관리자 사용자 ID 클레임이 존재하지 않는 경우, null을 반환합니다.
     * <p>
     * @param token 사용자 인증 정보를 포함하고 있는 JWT 토큰 문자열입니다.
     * @return 추출된 관리자 사용자 ID 문자열, 또는 클레임이 존재하지 않는 경우 null입니다.
     */

    public String getAdminUserInfoFromToken(String token) {
        Claims claims = parseClaims(token);
        if (claims != null) {
            return claims.get(CLAIM_ADMIN_USER_ID, String.class);
        }
        return null; // 클레임이 없거나 토큰 파싱 중 문제가 발생한 경우
    }


    /**
     * JWT 토큰을 기반으로 인증 정보를 생성합니다.
     * <p>
     * 이 메서드는 주어진 액세스 토큰에서 사용자의 인증 정보를 추출하여 Authentication 객체를 생성합니다.
     * 이 과정에서 사용자의 권한(역할)도 함께 추출하여 권한 정보를 설정합니다.
     *
     * @param accessToken 인증 정보를 추출하고자 하는 JWT 액세스 토큰입니다.
     * @return 생성된 사용자 인증 정보를 나타내는 Authentication 객체입니다.
     * @throws InvalidTokenException 권한 정보가 없거나 토큰 형식이 유효하지 않은 경우 예외를 발생시킵니다.
     */

    public Authentication getAuthentication(String accessToken) {
        // JWT 토큰을 해석하여 클레임(Claims) 객체를 추출합니다.
        Claims claims = parseClaims(accessToken);

        // 권한 정보가 없는 경우 예외를 발생시킵니다.
        if (Objects.requireNonNull(claims).get(CLAIM_IS_ADMIN) == null) {
            throw new InvalidTokenException("권한 정보가 없는 토큰입니다.");
        }

        // 권한 정보를 문자열로 변환합니다.
        String rolesStr = claims.get(CLAIM_IS_ADMIN).toString();
        Collection<? extends GrantedAuthority> authorities = getGrantedAuthorities(rolesStr);

        // UserDetails 객체를 생성하여 Authentication 객체를 반환합니다.
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 문자열로 주어진 권한 정보를 Spring Security의 GrantedAuthority 객체 컬렉션으로 변환합니다.
     * <p>
     * 입력된 권한 정보 문자열이 비어있는 경우, 기본 권한 'ROLE_DEFAULT'를 포함하는 컬렉션을 반환합니다.
     * 그렇지 않으면, 권한 정보를 쉼표로 분리하여 각 권한을 GrantedAuthority 객체로 변환하고, 이를 컬렉션에 추가합니다.
     *
     * @param rolesStr 쉼표로 구분된 권한 정보 문자열입니다.
     * @return 변환된 GrantedAuthority 객체의 컬렉션입니다.
     */

    private static Collection<? extends GrantedAuthority> getGrantedAuthorities(String rolesStr) {
        Collection<? extends GrantedAuthority> authorities;

        if (rolesStr.isEmpty()) {
            // 권한 정보가 비어있는 경우, 기본 권한을 설정합니다.
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEFAULT"));
        } else {
            // 권한 정보를 쉼표(,)로 분리하고 Spring Security의 GrantedAuthority로 변환합니다.
            List<SimpleGrantedAuthority> list = new ArrayList<>();
            for (String s : rolesStr.split(",")) {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(s);
                list.add(simpleGrantedAuthority);
            }
            authorities = list;
        }
        return authorities;
    }

    /**
     * 주어진 JWT 토큰을 파싱하고 유효성을 검사합니다.
     * <p>
     * 이 메서드는 JWT 토큰을 파싱하여 클레임을 추출합니다. allowExpired 매개변수에 따라 만료된 토큰의 처리 방식을 결정합니다.
     * 만료된 토큰을 허용하는 경우, 해당 토큰의 클레임을 반환합니다. 그렇지 않으면 CustomExpiredJwtException 예외를 발생시킵니다.
     * 유효하지 않은 토큰 형식이나 지원되지 않는 토큰인 경우, InvalidTokenException 예외를 발생시킵니다.
     *
     * @param token 파싱하고 유효성을 검사할 JWT 토큰 문자열입니다.
     * @param allowExpired 만료된 토큰을 허용할지 여부입니다.
     * @return 파싱된 클레임 객체입니다. 만료된 토큰이 허용되지 않는 경우, 예외가 발생합니다.
     * @throws InvalidTokenException 토큰이 유효하지 않은 경우 발생합니다.
     * @throws CustomExpiredJwtException 토큰이 만료되었고, 만료된 토큰이 허용되지 않는 경우 발생합니다.
     */

    public Claims parseAndValidateToken(String token, boolean allowExpired) throws InvalidTokenException, CustomExpiredJwtException {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
            Jws<Claims> jws = parser.parseClaimsJws(token);
            return jws.getBody();
        } catch (ExpiredJwtException e) {
            if (allowExpired) {
                // 만료된 토큰의 클레임을 반환할 수 있도록 허용
                return e.getClaims();
            } else {
                // 만료된 토큰에 대해 예외 처리
                throw new CustomExpiredJwtException("Expired JWT Token", e);
            }
        } catch (SecurityException | MalformedJwtException e) {
            throw new InvalidTokenException("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("JWT claims string is empty.", e);
        }
    }

    /**
     * 주어진 JWT 토큰의 유효성을 검사합니다.
     * <p>
     * 이 메서드는 주어진 JWT 토큰을 파싱하고 유효성을 검사하여 결과를 boolean 값으로 반환합니다.
     * 토큰이 유효한 경우 true를, 그렇지 않은 경우 false를 반환합니다.
     *
     * @param token 유효성을 검사할 JWT 토큰 문자열입니다.
     * @return 토큰의 유효성 검사 결과입니다. 유효한 경우 true, 그렇지 않은 경우 false입니다.
     */

    public boolean validateToken(String token) {
        try {
            parseAndValidateToken(token, false);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 주어진 JWT 액세스 토큰에서 클레임을 안전하게 파싱합니다.
     * <p>
     * 이 메서드는 주어진 액세스 토큰을 파싱하여 클레임을 추출합니다. 만료된 토큰도 허용되며,
     * 만료된 토큰의 경우, 해당 토큰의 클레임을 반환합니다. 파싱 중 문제가 발생한 경우,
     * CustomException 예외를 발생시킵니다.
     *
     * @param accessToken 클레임을 추출하고자 하는 JWT 액세스 토큰입니다.
     * @return 추출된 클레임 객체입니다. 토큰 파싱 중 문제가 발생한 경우, CustomException 예외가 발생합니다.
     * @throws CustomException 토큰 파싱 중 문제가 발생한 경우 발생합니다.
     */

    private Claims parseClaims(String accessToken) {
        try {
            return parseAndValidateToken(accessToken, true);
        } catch (InvalidTokenException | CustomExpiredJwtException e) {
            throw new CustomException("Token validation failed", e);
        }
    }












    /*


    public boolean validateToken(String token) {
        try {
            // 파서는 사용자 지정 키(key)를 사용하여 토큰을 검증합니다.
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key)
                    .build();
            // parseClaimsJws 메서드를 사용하여 JWT 토큰을 검증합니다.
            parser.parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 유효하지 않은 토큰 또는 형식 오류가 있는 경우 예외를 처리하고 사용자 정의 예외를 던집니다.
            log.info("Invalid JWT Token", e);
            throw new InvalidTokenException("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            // 만료된 토큰에 대한 처리를 수행할 수 있으며, 필요한 경우 예외를 처리합니다.
            throw new CustomExpiredJwtException("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰인 경우 예외를 처리하고 사용자 정의 예외를 던집니다.
            log.info("Unsupported JWT Token", e);
            throw new InvalidTokenException("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            // JWT 클레임 문자열이 비어있는 경우 예외를 처리하고 사용자 정의 예외를 던집니다.
            log.info("JWT claims string is empty.", e);
            throw new InvalidTokenException("JWT claims string is empty.", e);
        }

    }


    private Claims parseClaims(String accessToken) {
        try {
            // 파서는 사용자 지정 키(key)를 사용하여 토큰을 검증합니다.
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key)
                    .build();

            // parseClaimsJws 메서드를 사용하여 JWT 토큰을 해석하고, Jws<Claims> 객체를 반환합니다.
            Jws<Claims> jws = parser.parseClaimsJws(accessToken);

            return jws.getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 클레임을 반환합니다.
            return e.getClaims();
        }
    }

    */


}
