package com.lumeneditor.www.comm;

import com.lumeneditor.www.domain.auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component // 스프링 빈으로 등록
public class PasswordUtil {

    private static PasswordEncoder passwordEncoder;

    @Autowired // 생성자를 통한 의존성 주입
    public PasswordUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // User 객체의 비밀번호를 인코딩하고 설정하는 메서드 (이제 비정적 메서드로 변경)
    public static void encodeAndSetPassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodedPassword);
    }
}