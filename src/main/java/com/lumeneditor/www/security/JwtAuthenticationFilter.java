package com.lumeneditor.www.security;

import com.lumeneditor.www.exception.CustomExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * JWT 인증 필터의 핵심 메소드입니다. 이 메소드는 HTTP 요청이 들어올 때마다 실행되며,
     * 요청에서 JWT 토큰을 추출하여 검증한 후, 유효한 토큰일 경우 해당 사용자의 인증 정보를 SecurityContext에 저장합니다.
     * 이를 통해 요청이 서블릿이나 컨트롤러에 도달하기 전에 사용자가 인증되도록 합니다.
     *
     * @param request  서블릿 요청 객체
     * @param response 서블릿 응답 객체
     * @param chain    필터 체인 객체, 요청을 다음 필터 또는 서블릿으로 전달하기 위해 사용됩니다.
     * @throws IOException 입출력 작업 중 예외가 발생할 경우
     */

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {

        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            String requestURI = httpRequest.getRequestURI();
            // 지정된 경로에 대한 요청인지 확인
            if ("/auth/access-token".equals(requestURI) ||
                    "/auth/signup".equals(requestURI) ||
                    "/auth/send-auth-code".equals(requestURI) ||
                    "/auth/verify".equals(requestURI)) {
                // 지정된 경로에 대한 요청 처리를 계속 진행
                chain.doFilter(request, response);
                return;
            }

            // Request Header 또는 Cookie에서 JWT 토큰 추출
            String token = extractJwtFromRequest(httpRequest);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가져와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 요청을 다음 필터 또는 대상 서블릿으로 전달
            chain.doFilter(request, response);
        } catch (CustomExpiredJwtException e) {
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Token expired.\"}");
        } catch (Exception e) {
            // 다른 JWT 관련 예외 처리
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }


    /**
     * HttpServletRequest 객체에서 JWT 토큰을 추출하는 메소드입니다.
     * 이 메소드는 요청의 쿠키에서 'accessToken'이라는 이름의 쿠키를 찾아 그 값을 반환합니다.
     *
     * @param httpRequest 현재 HTTP 요청 객체
     * @return 찾은 JWT 토큰 문자열. 만약 'accessToken' 이름의 쿠키가 없다면 null을 반환합니다.
     */

    private String extractJwtFromRequest(HttpServletRequest httpRequest) {
        // Cookie에서 JWT 토큰 추출
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
