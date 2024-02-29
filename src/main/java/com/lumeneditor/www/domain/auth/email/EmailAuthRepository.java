package com.lumeneditor.www.domain.auth.email;

import com.lumeneditor.www.domain.auth.entity.EmailAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long> {

    /**
     * 주어진 인증 코드와 이메일 주소에 해당하는 EmailAuth 엔티티를 조회합니다.
     * <p>
     * 이 메서드는 인증 프로세스에서 사용자가 제공한 인증 코드와 이메일 주소를 기반으로
     * 해당하는 EmailAuth 엔티티를 데이터베이스에서 찾습니다. 조회 결과는 Optional로 감싸져 있어,
     * 조회된 EmailAuth 엔티티가 존재하는 경우 해당 엔티티를, 그렇지 않은 경우 빈 Optional을 반환합니다.
     * 이를 통해 NullPointer 예외 없이 안전하게 결과를 처리할 수 있습니다.
     * <p>
     * @param authCode 사용자로부터 받은 인증 코드
     * @param authEmail 사용자의 이메일 주소
     * @return 조회된 EmailAuth 엔티티를 담고 있는 Optional 객체. 조회된 엔티티가 없는 경우 빈 Optional 반환.
     */

    Optional<EmailAuth> findByAuthCodeAndAuthEmail(String authCode, String authEmail);

}