package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.comm.EmailUtils;
import com.lumeneditor.www.domain.auth.email.EmailAuthRepository;
import com.lumeneditor.www.domain.auth.email.EmailService;
import com.lumeneditor.www.exception.InvalidTokenException;
import com.lumeneditor.www.security.JwtTokenProvider;
import com.lumeneditor.www.web.dto.EmailAuth;
import com.lumeneditor.www.web.dto.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailService emailService;

    // 이메일 중복 체크
    @Override
    public ResponseEntity<Boolean> checkEmailDuplication(User user) {

        if (!EmailUtils.isValidEmail(user.getUserId())) {
            // 유효하지 않은 이메일 주소인 경우
            return ResponseEntity.badRequest().body(false); // BadRequest 상태와 함께 false 반환
        }

        try {

            Long count = authRepository.countByUserId(user.getUserId()); // UserService를 통해 이메일 중복 검사

            // 중복된 경우
            if (count > 0) {

                return ResponseEntity.ok(false); // 중복되었으므로 false 반환

            } else {

                new EmailAuth();
                EmailAuth emailAuth = EmailAuth.builder()
                        .authEmail(user.getUserId())
                        .authCode(EmailUtils.createCode())
                        .build();

                // 임시비밀번호 저장
                emailAuthRepository.save(emailAuth);

                // 이메일 발송
                emailService.sendAuthenticationCodeEmail(emailAuth.getAuthEmail(),emailAuth.getAuthCode());

                // 사용 가능한 경우
                return ResponseEntity.ok(true); // 중복되지 않았으므로 true 반환
            }

        } catch (Exception e) {

            log.error("Database access error during email duplication check: ", e);
            // ResponseEntity의 badRequest()를 사용하여 클라이언트에게 오류 상태를 알림
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 로그아웃
    @Override
    public void logout(String accessToken) {
        try {
            String userId = jwtTokenProvider.getAdminUserInfoFromToken(accessToken);
            // Redis에서 사용자 ID를 키로 사용하여 refresh token 조회
            String refreshToken = redisTemplate.opsForValue().get(userId);
            // Redis에서 refresh token 삭제
            if (refreshToken != null) redisTemplate.delete(userId);

        } catch (InvalidTokenException e) {
            log.error("Invalid token error during logout: ", e);
            // 적절한 처리 로직 (예: 사용자에게 오류 메시지 반환)
        } catch (DataAccessException e) {
            log.error("Database access error during logout: ", e);
            // 적절한 처리 로직 (예: 사용자에게 오류 메시지 반환)
        }
    }


}
