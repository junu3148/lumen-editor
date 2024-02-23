package com.lumeneditor.www.comm;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenUtil {
    private JwtTokenUtil() {
    }

    /**
     * HttpServletRequest에서 'accessToken' 이름의 쿠키를 찾아 토큰 값을 추출합니다.
     *
     * 이 메서드는 HTTP 요청에 포함된 쿠키 배열을 순회하며 'accessToken'이라는 이름의 쿠키를 찾습니다.
     * 찾은 경우, 해당 쿠키의 값을 반환합니다. 이 값은 일반적으로 사용자 인증에 사용되는 JWT 토큰일 수 있습니다.
     * 만약 'accessToken' 이름의 쿠키가 요청에 포함되어 있지 않다면, null을 반환합니다.
     *
     * @param request 현재 HTTP 요청을 나타내는 HttpServletRequest 객체입니다.
     *                이 객체에서 쿠키 정보를 추출합니다.
     * @return 찾은 'accessToken' 쿠키의 값. 만약 해당 이름의 쿠키가 없으면 null을 반환합니다.
     */

    public static String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
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