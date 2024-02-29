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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/")
public class AuthController {


    private static final String ACCESS_TOKEN = "accessToken";
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
        if (jwtToken != null && jwtToken.getAccessToken() != null && !jwtToken.getAccessToken().isEmpty()) {
            addCookie(response, ACCESS_TOKEN, jwtToken.getAccessToken(), 60 * 60 * 24); // 하루 동안 유효한 쿠키 설정
        }
        return ResponseEntity.ok().body(jwtToken);
    }

    // accessToken 재발급
    @PostMapping("access-token")
    ResponseEntity<JwtToken> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        JwtToken jwtToken = memberService.getAccessToken(request);
        deleteCookie(response, ACCESS_TOKEN); // 기존 쿠키 삭제
        if (jwtToken != null && jwtToken.getAccessToken() != null && !jwtToken.getAccessToken().isEmpty()) {
            addCookie(response, ACCESS_TOKEN, jwtToken.getAccessToken(), 60 * 60 * 24); // 새 쿠키 추가
        }
        return ResponseEntity.ok().body(jwtToken);
    }

    // 로그아웃
    @PostMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = JwtTokenUtil.extractTokenFromCookies(request);
        if (accessToken != null && !accessToken.isEmpty()) {
            deleteCookie(response, ACCESS_TOKEN); // 쿠키 삭제
            authService.logout(accessToken); // 로그아웃 처리
        }
        return ResponseEntity.noContent().build();
    }



    // 쿠키 추가 및 설정 메서드
    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge); // 쿠키 만료 시간 설정. 0이면 즉시 만료, 양수면 해당 초만큼 유지
        response.addCookie(cookie);
    }

    // 쿠키 삭제 메서드
    private void deleteCookie(HttpServletResponse response, String name) {
        addCookie(response, name, null, 0); // 쿠키 만료 시간을 0으로 설정하여 삭제
    }



/*   // 로그인
    @PostMapping("login")
    public ResponseEntity<JwtToken> login(@RequestBody User user, HttpServletResponse response) {
        JwtToken jwtToken = memberService.signInAndGenerateJwtToken(user);

        // accessToken이 존재할 경우에만 쿠키 생성 및 설정
        if (jwtToken != null && jwtToken.getAccessToken() != null && !jwtToken.getAccessToken().isEmpty()) {
            Cookie cookie = new Cookie("accessToken", jwtToken.getAccessToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            // 필요하다면 쿠키의 만료 시간도 설정할 수 있습니다. 예: cookie.setMaxAge(60 * 60 * 24); // 하루
            response.addCookie(cookie);
        }

        // JWT 토큰을 포함한 응답 반환
        return ResponseEntity.ok().body(jwtToken);
    }

    // accessToken 재발급
    @PostMapping("access-token")
    ResponseEntity<JwtToken> getAccessToken(HttpServletRequest request, HttpServletResponse response) {

        JwtToken jwtToken = memberService.getAccessToken(request);

        // 기존의 accessToken 쿠키 삭제
        Cookie deleteCookie = new Cookie("accessToken", null); // 쿠키 이름과 null 값을 설정
        deleteCookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정하여 즉시 만료
        deleteCookie.setPath("/"); // 쿠키 경로 설정
        response.addCookie(deleteCookie); // 응답에 쿠키 추가하여 클라이언트에 전송

        // 새로운 accessToken 쿠키 생성 및 설정
        if (jwtToken != null && jwtToken.getAccessToken() != null && !jwtToken.getAccessToken().isEmpty()) {
            Cookie newCookie = new Cookie("accessToken", jwtToken.getAccessToken());
            newCookie.setHttpOnly(true);
            newCookie.setPath("/");
            // 필요하다면 쿠키의 만료 시간도 설정할 수 있습니다. 예: newCookie.setMaxAge(60 * 60 * 24); // 하루
            response.addCookie(newCookie);
        }

        // JWT 토큰을 포함한 응답 반환
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
    }*/



}
