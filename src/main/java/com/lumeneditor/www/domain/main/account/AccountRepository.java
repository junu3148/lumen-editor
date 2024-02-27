package com.lumeneditor.www.domain.main.account;

import com.lumeneditor.www.domain.auth.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<User,Long> {

    // 유저 세부 정보
    User findByUserId(String userId);

    // 비밀번호 변경
    @Modifying
    @Query("UPDATE User u SET u.userPassword = :userPassword WHERE u.userId = :userId")
    int updateUserPasswordById(@Param("userPassword") String userPassword, @Param("userId") String userId);


}
