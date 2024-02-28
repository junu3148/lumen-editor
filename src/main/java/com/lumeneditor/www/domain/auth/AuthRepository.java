package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {

    // @Query("SELECT u FROM User u WHERE u.userKey = :userKey") 전체 조회
    // @Query(value = "SELECT * FROM user WHERE user_id = :email", nativeQuery = true) 네이티비 쿼리작성

    /**
     * 주어진 이메일(ID)을 사용하는 사용자의 수를 카운트합니다.
     * <p>
     * 이 메서드는 데이터베이스에서 주어진 사용자 ID(이메일)에 해당하는 사용자의 수를 카운트합니다.
     * 주로 이메일(사용자 ID)의 중복 여부를 체크할 때 사용됩니다. 만약 주어진 사용자 ID를 가진 사용자가 없을 경우, 0을 반환합니다.
     * <p>
     * 이 메서드는 사용자가 회원가입 시 사용하려는 이메일(ID)이 이미 사용 중인지 확인할 때 사용됩니다.
     *
     * @param userId 카운트할 사용자의 ID(이메일).
     * @return 주어진 사용자 ID를 가진 사용자의 수.
     */
    Long countByUserId(String userId);

    /**
     * 주어진 사용자 ID로 로그인을 시도할 때 사용될 사용자 정보를 조회합니다.
     * <p>
     * 이 메서드는 JPA의 @Query 어노테이션을 사용하여, 데이터베이스에서 주어진 사용자 ID에 해당하는 사용자 정보를 조회합니다.
     * 조회된 정보는 로그인 시도에 필요한 사용자 ID와 비밀번호만을 포함한 User 객체로 반환됩니다.
     * <p>
     * 이 메서드는 사용자가 로그인을 시도할 때, 입력한 사용자 ID와 비밀번호의 일치 여부를 확인하기 위해 사용됩니다.
     *
     * @param userId 조회할 사용자의 ID.
     * @return 조회된 사용자의 정보를 포함한 User 객체. 사용자 ID가 데이터베이스에 없을 경우 null을 반환할 수 있습니다.
     */
    @Query("SELECT new com.lumeneditor.www.domain.auth.entity.User(u.userId, u.userPassword, u.role) FROM User u WHERE u.userId = :userId")
    User findByUserId(@Param("userId") String userId);






}