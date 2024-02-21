package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.web.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {

    // 기본 제공 메서드
    //User findByUserId(String userId);

    // @Query("SELECT u FROM User u WHERE u.userKey = :userKey") 전체 조회
    // @Query(value = "SELECT * FROM user WHERE user_id = :email", nativeQuery = true) 네이티비 쿼리작성
    @Query("SELECT new com.lumeneditor.www.web.dto.User(u.userId, u.userPassword) FROM User u WHERE u.userId = :userId")
    User findByUserId(@Param("userId") String userId);


}