package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.security.JwtTokenProvider;
import com.lumeneditor.www.web.dto.User;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    //private final AdminRepository adminRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    //private final TokenRepository tokenRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN_MESSAGE = "Invalid or expired refresh token";
    private static final String ERROR_PROCESSING_MESSAGE = "An error occurred while processing the refresh token";
    private static final String INVALID_CREDENTIALS_MESSAGE = "인증에 실패하였습니다.";
    private static final String INVALID_EMAIL_MESSAGE = "아이디는 이메일 형식이어야 합니다.";


    // 사용자 로그인을 처리하고, JWT 토큰을 생성하여 반환
    @Override
    @Transactional
    public JwtToken signInAndGenerateJwtToken(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (!isValidEmail(username)) {
            return badRequestJwtToken(); // 변경된 부분
        }
        try {
            return authenticateAndGenerateToken(username, password);
        } catch (AuthenticationException e) {
            return unauthorizedJwtToken(INVALID_CREDENTIALS_MESSAGE); // 변경된 부분
        }
    }


    // 주어진 사용자 이름과 비밀번호를 사용하여 사용자를 인증하고, JWT 토큰을 생성
    private JwtToken authenticateAndGenerateToken(String username, String password) {
        Authentication authentication = authenticateUser(username, password);
        return jwtTokenProvider.generateToken(authentication);
    }

    // 사용자 이름과 비밀번호로 사용자를 인증
    private Authentication authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }


    // JWT 토큰을 포함한 ResponseEntity를 생성
    private ResponseEntity<JwtToken> buildResponseWithToken(JwtToken jwtToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION_HEADER, BEARER_PREFIX + jwtToken.getAccessToken());
        return new ResponseEntity<>(jwtToken, httpHeaders, HttpStatus.OK);
    }


    // 잘못된 요청에 대한 응답을 생성하여 반환
    private JwtToken badRequestJwtToken() {
        JwtToken errorToken = new JwtToken();
        errorToken.setErrorMessage(INVALID_EMAIL_MESSAGE);
        return errorToken; // JwtToken 객체를 직접 반환
    }

    // 인증되지 않은 요청에 대한 응답을 생성하여 반환
    private JwtToken unauthorizedJwtToken(String message) {
        JwtToken errorToken = new JwtToken();
        errorToken.setErrorMessage(message);
        return errorToken; // JwtToken 객체를 직접 반환
    }


/*

    // 리프레시 토큰의 유효성을 검사한 후, 새로운 액세스 토큰을 발행
    @Override
    @Transactional
    public ResponseEntity<String> refreshTokenCK(String refreshToken) {

        try {
            if (!isValidRefreshToken(refreshToken)) {
                return unauthorizedStringResponse(INVALID_TOKEN_MESSAGE);
            } else {

                Optional<RefreshToken> refreshTokenOpt = tokenRepository.refreshTokenCk(refreshToken);
                if (refreshTokenOpt.isPresent()) {
                    // 토큰이 존재하는 경우, 새로운 액세스 토큰을 생성
                    String newAccessToken = createNewAccessToken(refreshToken);
                    return buildResponseWithToken(newAccessToken);
                }
            }
        } catch (Exception e) {
            return internalServerErrorResponse();
        }
        // 토큰이 유효하지 않거나 조회되지 않는 경우, 적절한 응답 반환
        return unauthorizedStringResponse(INVALID_TOKEN_MESSAGE);
    }

    // 제공된 리프레시 토큰이 유효한지 검사
    private boolean isValidRefreshToken(String refreshToken) {
        return refreshToken != null && jwtTokenProvider.validateToken(refreshToken)
                && tokenRepository.refreshTokenCK(refreshToken).isPresent();
    }
   // 제공된 리프레시 토큰을 사용하여 새로운 액세스 토큰을 생성
    private String createNewAccessToken(String refreshToken) {
        Optional<RefreshToken> tokenData = tokenRepository.refreshTokenCK(refreshToken);
        return jwtTokenProvider.generateAccessToken(tokenData);
    }

    // 토큰을 사용하여 JWT 토큰을 포함한 ResponseEntity를 생성
    private ResponseEntity<String> buildResponseWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, BEARER_PREFIX + token);

        // 토큰 정보를 JSON 문자열 형태로 만들기
        // 실제 환경에서는 객체를 JSON으로 변환할 라이브러리를 사용하겠지만 여기서는 수동으로 구성
        String responseBody = "{\"accessToken\":\"" + token + "\"}";

        return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    }

        // 인증되지 않은 요청에 대한 문자열 응답을 생성하여 반환
    private ResponseEntity<String> unauthorizedStringResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    // 내부 서버 오류에 대한 응답을 생성하여 반환
    private ResponseEntity<String> internalServerErrorResponse() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR_PROCESSING_MESSAGE);
    }



    */

    // 이메일 형식 체크
    private boolean isValidEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

}