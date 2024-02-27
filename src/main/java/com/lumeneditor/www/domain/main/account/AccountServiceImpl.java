package com.lumeneditor.www.domain.main.account;

import com.lumeneditor.www.comm.JwtTokenUtil;
import com.lumeneditor.www.comm.PasswordUtil;
import com.lumeneditor.www.domain.auth.entity.User;
import com.lumeneditor.www.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;


    // 유저 세부 정보
    @Override
    @Transactional
    public ResponseEntity<User> getUser(HttpServletRequest request) {

        User user = accountRepository.findByUserId(getUserId(request));

        if (user != null) {
            user.setUserPassword(""); // 사용자 정보는 있지만, 비밀번호는 응답에서 제외
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build(); // 사용자를 찾을 수 없는 경우
        }
    }

    @Transactional
    public ResponseEntity<Boolean> updateUserDetails(HttpServletRequest request, User user) {

        System.out.println(user);

        String userId = getUserId(request); // HttpServletRequest에서 사용자 ID 추출
        User existingUser = accountRepository.findByUserId(userId); // 데이터베이스에서 사용자 정보 조회

        if (existingUser == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        // 비밀번호가 제공된 경우에만 인코딩하고 업데이트
        if (user.getUserPassword() != null) {
            PasswordUtil.encodeAndSetPassword(user);
            existingUser.setUserPassword(user.getUserPassword());
        }

        updateIfNotNull(user.getUserName(), existingUser::setUserName);
        updateIfNotNull(user.getPhoneNumber(), existingUser::setPhoneNumber);
        updateIfNotNull(user.getWithdrawalDate(), existingUser::setWithdrawalDate);
        updateIfNotNull(user.getBirthYear(), existingUser::setBirthYear);
        updateIfNotNull(user.getOccupation(), existingUser::setOccupation);
        updateIfNotNull(user.getCountry(), existingUser::setCountry);
        updateIfNotNull(user.getGender(), existingUser::setGender);
        updateIfNotNull(user.getEmailAccept(), existingUser::setEmailAccept);
        updateIfNotNull(user.getPromoAccept(), existingUser::setPromoAccept);
        updateIfNotNull(user.getUserStatus(), existingUser::setUserStatus);
        updateIfNotNull(user.getCompany(), existingUser::setCompany);
        updateIfNotNull(user.getIsDeleted(), existingUser::setIsDeleted);
        updateIfNotNull(user.getLogoImage(), existingUser::setLogoImage);
        updateIfNotNull(user.getPasswordRecovery(), existingUser::setPasswordRecovery);
        updateIfNotNull(user.getRole(), existingUser::setRole);

        accountRepository.save(existingUser);
        return ResponseEntity.ok(true); // 성공적으로 업데이트 완료
    }
    
    // 조건 처리
    private <T> void updateIfNotNull(T value, Consumer<T> updateMethod) {
        if (value != null) {
            updateMethod.accept(value);
        }
    }

    // 유저 정보 수정
    @Override
    @Transactional
    public ResponseEntity<Boolean> updateUserPassword(HttpServletRequest request, User user) {

        user.setUserId(getUserId(request));

        // 사용자 비밀번호 인코딩
        PasswordUtil.encodeAndSetPassword(user);

        int result = accountRepository.updateUserPasswordById(user.getUserPassword(), user.getUserId());
        return result > 0 ? ResponseEntity.ok(true) : ResponseEntity.ok(false);

    }

    // 쿠키에서 아이디 추출
    private String getUserId(HttpServletRequest request) {

        // extractTokenFromCookies 메서드를 사용하여 쿠키에서 accessToken 추출
        String accessToken = JwtTokenUtil.extractTokenFromCookies(request);

        return jwtTokenProvider.getAdminUserInfoFromToken(accessToken);
    }
}
