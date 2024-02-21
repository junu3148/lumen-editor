package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.web.dto.User;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/")
public class AuthController {

    private final MemberService memberService;


    @PostMapping("login")
    public ResponseEntity<JwtToken> login(@RequestBody User user){
        return memberService.signInAndGenerateJwtToken(user);
    }



}
