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

    // 유저 정보
    @GetMapping("user")
    public ResponseEntity<User> getUser(HttpServletRequest request) {
        return accountService.getUser(request);
    }

    // 유저정보 비밀번호 수정
    @PatchMapping("user")
    public ResponseEntity<Boolean> updateUserPassword(HttpServletRequest request, @RequestBody User user) {
        return accountService.updateUserDetails(request, user);
    }



}
