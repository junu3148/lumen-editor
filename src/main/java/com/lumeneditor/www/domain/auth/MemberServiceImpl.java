package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.comm.EmailUtils;
import com.lumeneditor.www.comm.JwtTokenUtil;
import com.lumeneditor.www.security.JwtTokenProvider;
import com.lumeneditor.www.domain.auth.entity.User;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {


    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthRepository authRepository;

    private static final String INVALID_CREDENTIALS_MESSAGE = "Authentication failed.";
    private static final String INVALID_EMAIL_MESSAGE = "The ID must be in the form of an email.";


    // 사용자 로그인을 처리하고, JWT 토큰을 생성하여 반환
    @Override
    @Transactional
    public JwtToken signInAndGenerateJwtToken(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (!EmailUtils.isValidEmail(username)) {
            return badRequestJwtToken(); // 변경된 부분
        }
        try {
            return authenticateAndGenerateToken(username, password);
        } catch (AuthenticationException e) {
            return unauthorizedJwtToken(); // 변경된 부분
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

    // 잘못된 요청에 대한 응답을 생성하여 반환
    private JwtToken badRequestJwtToken() {
        JwtToken errorToken = new JwtToken();
        errorToken.setErrorMessage(INVALID_EMAIL_MESSAGE);
        return errorToken; // JwtToken 객체를 직접 반환
    }

    // 인증되지 않은 요청에 대한 응답을 생성하여 반환
    private JwtToken unauthorizedJwtToken() {
        JwtToken errorToken = new JwtToken();
        errorToken.setErrorMessage(MemberServiceImpl.INVALID_CREDENTIALS_MESSAGE);
        return errorToken; // JwtToken 객체를 직접 반환
    }


    // 리프레시 토큰의 유효성을 검사한 후, 새로운 액세스 토큰을 발행
    @Override
    @Transactional
    public JwtToken getAccessToken(HttpServletRequest request) {
        String accessToken = JwtTokenUtil.extractTokenFromCookies(request);
        String userId = jwtTokenProvider.getAdminUserInfoFromToken(accessToken);
        String refreshToken = redisTemplate.opsForValue().get(userId);


        if (refreshToken != null) {
            // 토큰이 유효한 경우, 새로운 액세스 토큰 생성
            User user = authRepository.findByUserId(userId);
            String newAccessToken = jwtTokenProvider.generateAccessToken(user);
            JwtToken jwtToken = new JwtToken();
            jwtToken.setAccessToken(newAccessToken);
            return jwtToken;
        }
        return null;

    }


}