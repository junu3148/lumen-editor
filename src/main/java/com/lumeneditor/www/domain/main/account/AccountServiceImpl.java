package com.lumeneditor.www.domain.main.account;

import com.lumeneditor.www.comm.JwtTokenUtil;
import com.lumeneditor.www.security.JwtTokenProvider;
import com.lumeneditor.www.domain.auth.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Override
    @Transactional
    public ResponseEntity<Boolean> updateUserPassword(HttpServletRequest request, User user) {


        user.setUserId(getUserId(request));

        System.out.println(user);

        return null;
    }

    private String getUserId(HttpServletRequest request) {

        // extractTokenFromCookies 메서드를 사용하여 쿠키에서 accessToken 추출
        String accessToken = JwtTokenUtil.extractTokenFromCookies(request);

        return jwtTokenProvider.getAdminUserInfoFromToken(accessToken);
    }
}
