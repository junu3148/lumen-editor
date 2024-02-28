package com.lumeneditor.www.domain.main.account;


import com.lumeneditor.www.domain.auth.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main/")
public class AccountController {

    private final AccountService accountService;

    // 유저 세부 정보
    @GetMapping("user")
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        return accountService.getUser(request);
    }

    // 선택적 유저 정보 수정
    @PatchMapping("user/details")
    public ResponseEntity<Boolean> updateUser(HttpServletRequest request, @RequestBody User user) {
        return accountService.updateUserDetails(request, user);
    }

    // 유저정보 비밀번호 수정
    @PatchMapping("user/password")
    public ResponseEntity<Boolean> updateUserPassword(HttpServletRequest request, @RequestBody User user) {
        return accountService.updateUserPassword(request, user);
    }

    // 유저 탈퇴
    @PatchMapping("user/delete")
    public ResponseEntity<Boolean> deleteUser(HttpServletRequest request) {
        return accountService.deleteUser(request);
    }


}
