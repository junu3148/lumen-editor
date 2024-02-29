package com.lumeneditor.www.comm;

import com.lumeneditor.www.domain.auth.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {
    private PasswordUtil() {
    }

    /**
     * 사용자의 비밀번호를 인코딩하고, 인코딩된 비밀번호로 사용자 객체를 업데이트합니다.
     * <p>
     * 이 메서드는 SpringContextUtil을 통해 PasswordEncoder 빈을 가져온 후,
     * 주어진 사용자의 비밀번호를 인코딩합니다. 인코딩된 비밀번호는 다시 사용자 객체에 설정됩니다.
     * 이 과정을 통해, 사용자의 원본 비밀번호는 보안이 강화된 인코딩된 형태로 저장될 수 있습니다.
     * <p>
     * @param user 비밀번호를 인코딩하고자 하는 사용자 객체입니다. 사용자 객체는 비밀번호 정보를 포함해야 합니다.
     */

    public static void encodeAndSetPassword(User user) {
        PasswordEncoder passwordEncoder = SpringContextUtil.getBean(PasswordEncoder.class);
        String encodedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodedPassword);
    }
}