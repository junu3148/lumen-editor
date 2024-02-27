package com.lumeneditor.www.domain.auth.email;

import com.lumeneditor.www.domain.auth.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    // 인증 코드와 이메일 주소로 EmailAuth 엔티티 조회
    Optional<EmailAuth> findByAuthCodeAndAuthEmail(String authCode, String authEmail);

}