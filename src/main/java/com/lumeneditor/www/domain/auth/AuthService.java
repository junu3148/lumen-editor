package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.web.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {

    /**
     * 클라이언트로부터 전달받은 이메일 주소의 중복 여부를 검사합니다.
     * <p>
     * 이 메서드는 클라이언트로부터 User 객체를 받아오며, 이 User 객체 내에는 검사하고자 하는
     * 이메일 주소가 포함되어 있습니다. 메서드는 이 이메일 주소를 데이터베이스와 비교하여,
     * 해당 이메일 주소가 이미 존재하는지 여부를 확인합니다.
     * <p>
     * 이메일 주소가 데이터베이스에 이미 존재하는 경우, "Duplicate email." 메시지와 함께
     * ResponseEntity를 클라이언트에게 반환합니다. 이메일 주소가 존재하지 않는 경우,
     * "Email available." 메시지를 포함한 ResponseEntity를 반환하여,
     * 사용자가 해당 이메일 주소를 사용할 수 있음을 알립니다.
     *
     * @param user 클라이언트로부터 받은 User 객체. 이 객체의 email 필드에는
     *             중복 검사를 진행할 이메일 주소가 포함되어 있습니다.
     * @return 이메일 주소의 중복 여부에 따라 적절한 메시지와 상태 코드를 포함한 ResponseEntity 객체.
     */

    ResponseEntity<Boolean> checkEmailDuplication(@RequestBody User user);


    /**
     * 액세스 토큰을 기반으로 사용자 로그아웃 처리를 수행합니다.
     * 이 메서드는 액세스 토큰을 파라미터로 받아와서, 해당 토큰에 연결된 사용자의 정보를 추출합니다.
     * 추출된 사용자 ID를 사용하여 Redis에서 해당 사용자의 리프레시 토큰을 조회하고,
     * 리프레시 토큰이 존재할 경우 Redis에서 해당 키를 삭제합니다.
     * 이로써 사용자는 더 이상 해당 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받을 수 없게 됩니다.
     * 추가적인 로그아웃 관련 처리가 필요할 경우, 이 메서드 내에서 수행할 수 있습니다.
     *
     * @param accessToken 로그아웃하려는 사용자의 액세스 토큰. 사용자의 식별 정보를 추출하기 위해 사용됩니다.
     */

    void logout(String accessToken);

}
