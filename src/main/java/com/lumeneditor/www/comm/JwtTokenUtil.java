package com.lumeneditor.www.comm;

import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenUtil {

    private JwtTokenUtil() {
    }

    // 토큰 분리
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


}