package com.lumeneditor.www.domain.main.account;

import com.lumeneditor.www.comm.JwtTokenUtil;
import com.lumeneditor.www.comm.PasswordUtil;
import com.lumeneditor.www.comm.eunm.YesNo;
import com.lumeneditor.www.domain.auth.entity.User;
import com.lumeneditor.www.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;


    // 유저 세부 정보
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<User> getUser(HttpServletRequest request) {

        User user = accountRepository.findByUserId(getUserId(request));

        if (user != null) {
            user.setUserPassword(""); // 사용자 정보는 있지만, 비밀번호는 응답에서 제외
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build(); // 사용자를 찾을 수 없는 경우
        }
    }

    // 선택적 유저 정보 수정
    @Override
    @Transactional
    public ResponseEntity<Boolean> updateUserDetails(HttpServletRequest request, User user) {

        String userId = getUserId(request); // HttpServletRequest에서 사용자 ID 추출
        User existingUser = accountRepository.findByUserId(userId); // 데이터베이스에서 사용자 정보 조회

        if (existingUser == null) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        boolean updated = false; // 변경 사항이 있는지 추적하는 플래그

        // 각 필드에 대한 변경 사항을 확인하고 필요한 경우 업데이트합니다.
        updated |= updateIfNotNull(user.getPhoneNumber(), existingUser::setPhoneNumber);
        updated |= updateIfNotNull(user.getBirthYear(), existingUser::setBirthYear);
        updated |= updateIfNotNull(user.getOccupation(), existingUser::setOccupation);
        updated |= updateIfNotNull(user.getCountry(), existingUser::setCountry);
        updated |= updateIfNotNull(user.getGender(), existingUser::setGender);
        updated |= updateIfNotNull(user.getEmailAccept(), existingUser::setEmailAccept);
        updated |= updateIfNotNull(user.getPromoAccept(), existingUser::setPromoAccept);
        updated |= updateIfNotNull(user.getCompany(), existingUser::setCompany);
        updated |= updateIfNotNull(user.getLogoImage(), existingUser::setLogoImage);
        updated |= updateIfNotNull(user.getRole(), existingUser::setRole);

        accountRepository.save(existingUser);

        return ResponseEntity.ok(updated); // 변경 사항이 있으면 true, 없으면 false 반환
    }

    // 조건 처리를 위해 수정된 메서드
    private <T> boolean updateIfNotNull(T value, Consumer<T> updateMethod) {
        if (value != null) {
            updateMethod.accept(value);
            return true; // 값이 null이 아니라면 변경 사항이 있으므로 true 반환
        }
        return false; // 변경 사항이 없으므로 false 반환
    }

    // 비밀번호 수정
    @Override
    @Transactional
    public ResponseEntity<Boolean> updateUserPassword(HttpServletRequest request, User user) {

        user.setUserId(getUserId(request));

        // 사용자 비밀번호 인코딩
        PasswordUtil.encodeAndSetPassword(user);

        int result = accountRepository.updateUserPasswordById(user.getUserPassword(), user.getUserId());
        return result > 0 ? ResponseEntity.ok(true) : ResponseEntity.ok(false);

    }

    // 회원 탈퇴
    @Override
    @Transactional
    public ResponseEntity<Boolean> deleteUser(HttpServletRequest request) {
        String userId = getUserId(request); // HttpServletRequest에서 사용자 ID 추출

        // 사용자 상태, 삭제 여부, 탈퇴 날짜를 업데이트
        int updatedCount = accountRepository.deleteUser(userId, YesNo.N, 1);

        if (updatedCount == 0) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        return ResponseEntity.ok(true); // 성공적으로 처리됨을 나타내는 응답 반환
    }




    // 쿠키에서 아이디 추출
    private String getUserId(HttpServletRequest request) {

        // extractTokenFromCookies 메서드를 사용하여 쿠키에서 accessToken 추출
        String accessToken = JwtTokenUtil.extractTokenFromCookies(request);

        return jwtTokenProvider.getAdminUserInfoFromToken(accessToken);
    }
}
