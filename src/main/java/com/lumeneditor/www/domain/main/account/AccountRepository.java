package com.lumeneditor.www.domain.main.account;

import com.lumeneditor.www.comm.eunm.YesNo;
import com.lumeneditor.www.domain.auth.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<User,Long> {

    /**
     * 사용자 ID를 기준으로 사용자 세부 정보를 조회합니다.
     * <p>
     * 이 메서드는 데이터베이스에서 주어진 사용자 ID에 해당하는 사용자의 세부 정보를 조회합니다.
     * 조회된 사용자 정보는 User 객체로 반환됩니다. 만약 해당 사용자 ID에 대한 정보가 데이터베이스에 없을 경우, null을 반환할 수 있습니다.
     * <p>
     * 이 메서드는 사용자의 프로필 페이지나 사용자 정보 관리 기능에서 사용자의 세부 정보를 표시할 때 사용됩니다.
     *
     * @param userId 조회할 사용자의 ID.
     * @return 조회된 사용자의 User 객체 또는 사용자 정보가 없을 경우 null.
     */
    User findByUserId(String userId);

    /**
     * 사용자의 비밀번호를 업데이트하는 쿼리 메서드입니다.
     * <p>
     * 이 메서드는 JPA의 @Query 어노테이션을 사용하여, 데이터베이스에 직접 쿼리를 실행합니다.
     * 주어진 사용자 ID에 해당하는 사용자의 비밀번호를 새로운 값으로 업데이트하고, 비밀번호 복구 날짜를 현재 날짜로 설정합니다.
     * <p>
     * 비밀번호 변경 작업이 성공적으로 완료되면, 업데이트된 레코드의 수를 정수로 반환합니다. 일반적으로 이 값은 1이 됩니다.
     * 만약 해당 사용자 ID를 가진 사용자가 없으면, 업데이트되지 않으므로 반환값은 0이 됩니다.
     *
     * @param userPassword 변경할 사용자의 새 비밀번호.
     * @param userId 변경할 사용자의 ID.
     * @return 업데이트된 레코드의 수.
     */
    @Modifying
    @Query("UPDATE User u SET u.userPassword = :userPassword, u.passwordRecovery = CURRENT_DATE WHERE u.userId = :userId")
    int updateUserPasswordById(@Param("userPassword") String userPassword, @Param("userId") String userId);

    /**
     * 사용자의 회원 탈퇴를 처리하는 쿼리 메서드입니다.
     * <p>
     * 이 메서드는 JPA의 @Query 어노테이션을 사용하여, 데이터베이스에 직접 쿼리를 실행합니다.
     * 주어진 사용자 ID에 해당하는 사용자의 상태를 업데이트하여 회원 탈퇴 처리를 합니다. 사용자 상태, 삭제 플래그를 업데이트하고,
     * 탈퇴 날짜를 현재 날짜로 설정합니다.
     * <p>
     * 회원 탈퇴 처리가 성공적으로 완료되면, 업데이트된 레코드의 수를 정수로 반환합니다. 일반적으로 이 값은 1이 됩니다.
     * 만약 해당 사용자 ID를 가진 사용자가 없으면, 업데이트되지 않으므로 반환값은 0이 됩니다.
     *
     * @param userId 탈퇴 처리할 사용자의 ID.
     * @param status 업데이트할 사용자의 상태.
     * @param isDeleted 사용자의 삭제 플래그(삭제된 경우 1, 그렇지 않은 경우 0).
     * @return 업데이트된 레코드의 수.
     */
    @Modifying
    @Query("UPDATE User u SET u.userStatus = :status, u.isDeleted = :isDeleted, u.withdrawalDate = CURRENT_DATE WHERE u.userId = :userId")
    int deleteUser(String userId, YesNo status, int isDeleted);

}
