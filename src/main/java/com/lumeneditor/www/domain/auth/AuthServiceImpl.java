package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.comm.EmailUtils;
import com.lumeneditor.www.domain.auth.email.EmailAuthRepository;
import com.lumeneditor.www.domain.auth.email.EmailService;
import com.lumeneditor.www.exception.InvalidTokenException;
import com.lumeneditor.www.security.JwtTokenProvider;
import com.lumeneditor.www.domain.auth.entity.EmailAuth;
import com.lumeneditor.www.domain.auth.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

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



    // 회원가입 인증번호 확인
    @Override
    @Transactional
    public ResponseEntity<Boolean> verifyAuthenticationCode(EmailAuth emailAuth) {

        // findByAuthCodeAndAuthEmail 메서드를 사용하여 authCode와 authEmail에 해당하는 EmailAuth 객체를 검색
        Optional<EmailAuth> result = emailAuthRepository.findByAuthCodeAndAuthEmail(emailAuth.getAuthCode(), emailAuth.getAuthEmail());

        // 결과가 존재하는지 확인
        if (result.isPresent()) {
            // 결과가 있으면 true 반환
            return ResponseEntity.ok(true);
        } else {
            // 결과가 없으면 false 반환
            return ResponseEntity.ok(false);
        }
    }

    // 회원가입
    @Override
    @Transactional
    public ResponseEntity<Boolean> signUp(User user) {
        try {

            // 사용자 비밀번호 인코딩
            String encodedPassword = passwordEncoder.encode(user.getUserPassword());
            user.setUserPassword(encodedPassword);

            authRepository.save(user);
            return ResponseEntity.ok(true); // 성공적으로 저장되었을 때 true 반환
        } catch (DataIntegrityViolationException e) {
            // 데이터베이스 제약 조건 위반 등의 예외 처리
            return ResponseEntity.badRequest().body(false); // 저장 실패 시 false 반환
        } catch (Exception e) {
            // 기타 예외 처리
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR); // 내부 서버 오류 시 false 반환
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
