package com.lumeneditor.www.domain.main.account;

import com.lumeneditor.www.domain.auth.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AccountService {

    /**
     * 현재 세션의 사용자 정보를 조회합니다.
     * <p>
     * 이 메서드는 HTTP 요청을 통해 전달받은 HttpServletRequest 객체를 사용하여 현재 세션에 로그인한 사용자의 정보를 조회합니다.
     * 세션에서 사용자 식별 정보(예: 사용자 ID)를 추출하고, 이를 사용하여 데이터베이스에서 해당 사용자의 정보를 검색합니다.
     * <p>
     * 사용자 정보가 성공적으로 검색된 경우, 해당 사용자의 User 객체를 포함한 ResponseEntity를 클라이언트에게 반환합니다.
     * 만약 사용자 정보를 검색할 수 없거나 세션이 유효하지 않은 경우, 적절한 상태 코드와 함께 에러 메시지를 담은 ResponseEntity를 반환합니다.
     * <p>
     * 이 메서드는 사용자가 시스템에 로그인한 후 자신의 프로필 정보를 조회하고자 할 때 사용됩니다.
     *
     * @param request 클라이언트로부터 받은 HttpServletRequest 객체. 현재 세션의 사용자 식별 정보를 포함하고 있습니다.
     * @return 사용자 정보 조회 성공 여부에 따라 사용자 정보 또는 에러 메시지를 포함한 ResponseEntity 객체.
     */

    ResponseEntity<User> getUser(HttpServletRequest request);


    ResponseEntity<Boolean> updateUserPassword(HttpServletRequest request, User user);

    ResponseEntity<Boolean> updateUserDetails(HttpServletRequest request, User user);


}

