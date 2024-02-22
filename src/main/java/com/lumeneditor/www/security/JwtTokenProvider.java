package com.lumeneditor.www.security;

import com.lumeneditor.www.config.RedisConfig;
import com.lumeneditor.www.exception.CustomExpiredJwtException;
import com.lumeneditor.www.exception.InvalidTokenException;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import com.lumeneditor.www.web.dto.auth.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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

@Slf4j
@Service
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private final Key key;
    private final long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30분
    private final long REFRESH_TOKEN_EXPIRE_COUNT = 8 * 60 * 60 * 1000L; // 8시간
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
        // TokenRepository 인스턴스를 할당합니다.

        // TokenRepository 인스턴스를 할당합니다.
    }


    /**
     * 인증된 사용자의 Authentication 객체를 받아 JWT 액세스 토큰과 리프레시 토큰을 생성합니다.
     * 이 메서드는 사용자의 권한을 기반으로 JWT 액세스 토큰을 생성하며, 해당 토큰은 30분 동안 유효합니다.
     * 또한, 사용자를 위한 리프레시 토큰을 생성하거나 기존에 존재하는 리프레시 토큰을 조회하여 반환합니다.
     * 리프레시 토큰은 8시간 동안 유효합니다.
     *
     * @param authentication 인증된 사용자의 Authentication 객체. 사용자의 이름과 권한 정보를 포함합니다.
     * @return 생성된 JWT 액세스 토큰이 포함된 JwtToken 객체.
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
     * 주어진 RefreshToken 객체를 이용하여 새로운 액세스 토큰을 생성합니다.
     * 이 메서드는 RefreshToken 객체로부터 사용자의 이름과 역할을 추출하여
     * JWT 토큰을 생성합니다. 생성된 토큰은 30분 동안 유효합니다.
     *
     * @param tokenData Optional<RefreshToken> 형태로 제공되는 토큰 데이터.
     *                  이 데이터는 사용자의 이름과 역할 정보를 포함하고 있어야 합니다.
     * @return 생성된 JWT 액세스 토큰 문자열.
     */
    public String generateAccessToken(Optional<RefreshToken> tokenData) {
        if (tokenData.isEmpty()) {
            throw new IllegalArgumentException("Token data must be present");
        }


        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_COUNT); // 액세스 토큰 유효 시간: 30분

        return Jwts.builder()
                .setHeaderParam("typ", TOKEN_TYPE)
                .setSubject(tokenData.get().getUsername()) // tokenData가 존재한다는 것이 검증되었으므로 get() 호출이 안전
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SIGNATURE_ALGORITHM)
                .compact();
    }

    /**
     * JWT 토큰에서 관리자 사용자 정보를 추출하여 adminId 반환합니다.
     *
     * @param token JWT 토큰 문자열.
     * @return adminId 반환
     */
    public String getAdminUserInfoFromToken(String token) {

        // 토큰을 파싱하여 Claims 객체를 얻습니다.
        Claims claims = parseToken(token);
        // 완성된 AdminUser 객체를 반환합니다.
        return claims.get(CLAIM_ADMIN_USER_ID, String.class);

    }

    /**
     * 주어진 JWT 토큰을 파싱하여 Claims 객체를 반환합니다.
     * 이 메서드는 JWT 토큰의 유효성을 검증하고, 토큰 내부에 저장된 클레임(claim)들을 추출합니다.
     *
     * @param token 파싱할 JWT 토큰 문자열.
     * @return 토큰에서 추출된 Claims 객체.
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않거나 파싱 중 문제가 발생한 경우.
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key) // JWT 토큰을 검증하기 위한 서명 키 설정
                .build()           // JwtParserBuilder 인스턴스를 JwtParser로 빌드
                .parseClaimsJws(token) // 토큰을 파싱하여 Claims JWS 객체를 얻음
                .getBody();            // Claims JWS 객체에서 Claims(클레임 세트)를 추출
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        return claims.getExpiration();
    }

    /**
     * 주어진 JWT 액세스 토큰을 사용하여 인증(Authentication) 객체를 생성하여 반환합니다.
     * 이 메서드는 JWT 토큰에서 추출한 권한 정보를 사용하여 Spring Security의 Authentication 객체를 생성합니다.
     *
     * @param accessToken JWT 액세스 토큰 문자열.
     * @return 생성된 Authentication 객체.
     * @throws InvalidTokenException 토큰이 유효하지 않거나 권한 정보가 없는 경우 발생할 수 있는 예외.
     */
    public Authentication getAuthentication(String accessToken) {
        // JWT 토큰을 해석하여 클레임(Claims) 객체를 추출합니다.
        Claims claims = parseClaims(accessToken);

        // 권한 정보가 없는 경우 예외를 발생시킵니다.
        if (claims.get(CLAIM_IS_ADMIN) == null) {
            throw new InvalidTokenException("권한 정보가 없는 토큰입니다.");
        }

        // 권한 정보를 문자열로 변환합니다.
        String rolesStr = claims.get(CLAIM_IS_ADMIN).toString();
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

        // UserDetails 객체를 생성하여 Authentication 객체를 반환합니다.
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 주어진 JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰 문자열.
     * @return 토큰이 유효한 경우 true, 그렇지 않은 경우 false 반환.
     * @throws InvalidTokenException 유효하지 않은 토큰인 경우 발생할 수 있는 사용자 정의 예외.
     */
    public boolean validateToken(String token) {
        try {
            // JWT 토큰을 검증하기 위한 파서를 생성합니다.
            // 파서는 사용자 지정 키(key)를 사용하여 토큰을 검증합니다.
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key) // 사용자 지정 키로 서명을 검증합니다.
                    .build();

            // parseClaimsJws 메서드를 사용하여 JWT 토큰을 검증합니다.
            parser.parseClaimsJws(token);

            // 토큰이 유효한 경우 true를 반환합니다.
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

    /**
     * 주어진 JWT 토큰을 파싱하여 Claims 객체를 반환합니다.
     * 이 메서드는 JWT 토큰의 유효성을 검증하고, 토큰 내부에 저장된 클레임(claim)들을 추출합니다.
     *
     * @param accessToken 파싱할 JWT 토큰 문자열.
     * @return 토큰에서 추출된 Claims 객체.
     * @throws io.jsonwebtoken.JwtException 토큰이 유효하지 않거나 파싱 중 문제가 발생한 경우.
     */
    private Claims parseClaims(String accessToken) {
        try {
            // JWT 토큰을 해석하기 위한 파서를 생성합니다.
            // 파서는 사용자 지정 키(key)를 사용하여 토큰을 검증합니다.
            JwtParser parser = Jwts.parserBuilder().setSigningKey(key) // 사용자 지정 키로 서명을 검증합니다.
                    .build();

            // parseClaimsJws 메서드를 사용하여 JWT 토큰을 해석하고, Jws<Claims> 객체를 반환합니다.
            Jws<Claims> jws = parser.parseClaimsJws(accessToken);

            // 만약 토큰이 만료된 경우, ExpiredJwtException 예외가 발생할 수 있습니다.
            // 이 예외를 처리하여 만료된 토큰에 포함된 클레임을 반환합니다.
            return jws.getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 클레임을 반환합니다.
            return e.getClaims();
        }
    }

}
