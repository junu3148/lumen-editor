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

    /**
     * 현재 세션의 사용자 비밀번호를 업데이트합니다.
     * <p>
     * 이 메서드는 HTTP 요청을 통해 전달받은 HttpServletRequest 객체와 사용자의 새 비밀번호 정보가 담긴 User 객체를 사용합니다.
     * 세션에서 사용자 식별 정보(예: 사용자 ID)를 추출하고, 사용자가 제공한 새 비밀번호로 데이터베이스에 저장된 비밀번호를 업데이트합니다.
     * <p>
     * 비밀번호 업데이트가 성공적으로 이루어진 경우, true를 포함한 ResponseEntity를 클라이언트에게 반환합니다.
     * 만약 사용자 정보를 찾을 수 없거나 기타 오류가 발생한 경우, false를 포함한 ResponseEntity를 반환합니다.
     * <p>
     * 이 메서드는 사용자가 자신의 비밀번호를 변경하고자 할 때 사용됩니다.
     *
     * @param request 클라이언트로부터 받은 HttpServletRequest 객체. 현재 세션의 사용자 식별 정보를 포함하고 있습니다.
     * @param user 사용자가 제공한 새 비밀번호 정보가 담긴 User 객체.
     * @return 비밀번호 업데이트 성공 여부를 포함한 ResponseEntity 객체.
     */

    ResponseEntity<Boolean> updateUserPassword(HttpServletRequest request, User user);

    /**
     * 현재 세션의 사용자 상세 정보를 업데이트합니다.
     * <p>
     * 이 메서드는 HTTP 요청을 통해 전달받은 HttpServletRequest 객체와 업데이트할 사용자의 상세 정보가 담긴 User 객체를 사용합니다.
     * 세션에서 사용자 식별 정보(예: 사용자 ID)를 추출하고, 제공된 User 객체의 정보로 데이터베이스에 저장된 사용자 정보를 업데이트합니다.
     * <p>
     * 상세 정보 업데이트가 성공적으로 이루어진 경우, true를 포함한 ResponseEntity를 클라이언트에게 반환합니다.
     * 만약 사용자 정보를 찾을 수 없거나 기타 오류가 발생한 경우, false를 포함한 ResponseEntity를 반환합니다.
     * <p>
     * 이 메서드는 사용자가 자신의 프로필 정보를 업데이트하고자 할 때 사용됩니다.
     *
     * @param request 클라이언트로부터 받은 HttpServletRequest 객체. 현재 세션의 사용자 식별 정보를 포함하고 있습니다.
     * @param user 업데이트할 사용자 상세 정보가 담긴 User 객체.
     * @return 상세 정보 업데이트 성공 여부를 포함한 ResponseEntity 객체.
     */

    ResponseEntity<Boolean> updateUserDetails(HttpServletRequest request, User user);

    /**
     * 현재 세션의 사용자를 삭제합니다.
     * <p>
     * 이 메서드는 HTTP 요청을 통해 전달받은 HttpServletRequest 객체를 사용하여 현재 세션에 로그인한 사용자를 데이터베이스에서 삭제합니다.
     * 세션에서 사용자 식별 정보(예: 사용자 ID)를 추출하고, 해당 사용자를 데이터베이스에서 삭제합니다.
     * <p>
     * 사용자 삭제가 성공적으로 이루어진 경우, true를 포함한 ResponseEntity를 클라이언트에게 반환합니다.
     * 만약 사용자 정보를 찾을 수 없거나 기타 오류가 발생한 경우, false를 포함한 ResponseEntity를 반환합니다.
     * <p>
     * 이 메서드는 관리자가 특정 사용자를 시스템에서 제거하고자 할 때 사용됩니다.
     *
     * @param request 클라이언트로부터 받은 HttpServletRequest 객체. 현재 세션의 사용자 식별 정보를 포함하고 있습니다.
     * @return 사용자 삭제 성공 여부를 포함한 ResponseEntity 객체.
     */

    ResponseEntity<Boolean> deleteUser(HttpServletRequest request);


}

