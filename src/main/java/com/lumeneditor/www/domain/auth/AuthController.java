package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.comm.JwtTokenUtil;
import com.lumeneditor.www.domain.auth.entity.EmailAuth;
import com.lumeneditor.www.domain.auth.entity.User;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/")
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;


    // 이메일 중복 체크
    @PostMapping("send-auth-code")
    public ResponseEntity<Boolean> checkEmailDuplication(@RequestBody User user) {
        return authService.checkEmailDuplication(user);
    }

    // 회원가입 인증번호 확인
    @PostMapping("verify")
    public ResponseEntity<Boolean> verify(@RequestBody EmailAuth emailAuth) {
        return authService.verifyAuthenticationCode(emailAuth);
    }

    // 회원 가입
    @PostMapping("signup")
    public ResponseEntity<Boolean> signUp(@RequestBody User user) {
        return authService.signUp(user);
    }


    // 로그인
    @PostMapping("login")
    public ResponseEntity<JwtToken> login(@RequestBody User user, HttpServletResponse response) {

        JwtToken jwtToken = memberService.signInAndGenerateJwtToken(user);

        // 쿠키 생성 및 설정
        Cookie cookie = new Cookie("accessToken", jwtToken.getAccessToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // 필요하다면 쿠키의 만료 시간도 설정할 수 있습니다. 예: cookie.setMaxAge(60 * 60 * 24); // 하루
        response.addCookie(cookie);

        // 쿠키를 포함한 응답 반환
        return ResponseEntity.ok().body(jwtToken);
    }


    // 로그아웃
    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // extractTokenFromCookies 메서드를 사용하여 쿠키에서 accessToken 추출
        String accessToken = JwtTokenUtil.extractTokenFromCookies(request);

        // accessToken이 있는 경우, 쿠키를 만료시키고 AuthService의 logout을 호출
        if (accessToken != null && !accessToken.isEmpty()) {
            // 쿠키 만료시키기
            Cookie cookie = new Cookie("accessToken", null); // null 값을 가진 새 쿠키 생성
            cookie.setPath("/");  // 쿠키 경로 설정
            cookie.setHttpOnly(true);  // 쿠키에 HttpOnly 설정
            cookie.setMaxAge(0);  // 쿠키 만료시키기
            response.addCookie(cookie);  // 응답에 수정된 쿠키 추가

            // AuthService를 통해 추가적인 로그아웃 처리 수행
            authService.logout(accessToken);  // accessToken을 인자로 넘겨서 로그아웃 처리
        }

        // 응답 반환
        return ResponseEntity.noContent().build();
    }


}
