package com.lumeneditor.www.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumeneditor.www.domain.auth.entity.User;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class) // AuthController에 대한 웹 계층 테스트를 설정합니다.
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // Spring MVC 테스트의 핵심 클래스로, 웹 요청을 모의로 보내고 응답을 받습니다.

    @MockBean
    private MemberService memberService; // MemberService의 모의 객체를 생성합니다.

    @MockBean
    private AuthService authService; // AuthService의 모의 객체를 생성합니다.

    @Autowired
    private WebApplicationContext context; // 웹 애플리케이션 컨텍스트를 주입받습니다.

    @BeforeEach
    public void setup() {
        // 각 테스트 실행 전에 MockMvc 인스턴스를 WebApplicationContext를 사용하여 구성합니다.
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testLogin() throws Exception {
        // 로그인 테스트를 위한 JwtToken 준비
        JwtToken jwtToken = JwtToken.builder()
                .accessToken("dummyAccessToken") // 토큰 값 예시
                .grantType("bearer") // 토큰 타입
                .refreshToken("dummyRefreshToken") // 새로 고침 토큰 값 예시
                .errorMessage("") // 에러 메시지 필드는 비어 있음
                .role(1) // 역할을 나타내는 더미 값
                .build();

        // MemberService가 특정 User 객체에 대해 호출될 때 jwtToken을 반환하도록 설정
        given(memberService.signInAndGenerateJwtToken(any(User.class))).willReturn(jwtToken);

        // 로그인 요청을 위한 User 객체 생성
        User user = new User();
        user.setUserId("testUser");
        user.setUserPassword("password");

        // User 객체를 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        // /auth/login 경로로 POST 요청을 보내고 응답을 검증
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk()) // 상태 코드가 200 OK인지 검증
                .andExpect(cookie().httpOnly("accessToken", true)) // 응답 쿠키가 HttpOnly인지 검증
                .andExpect(cookie().exists("accessToken")); // accessToken 쿠키가 존재하는지 검증
    }

    @Test
    void testLogout() throws Exception {
        // /auth/logout 경로로 POST 요청을 보내고 응답을 검증
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isNoContent()); // 상태 코드가 204 No Content인지 검증
        // authService.logout 호출 여부를 추가로 검증할 수 있습니다.
    }
}
