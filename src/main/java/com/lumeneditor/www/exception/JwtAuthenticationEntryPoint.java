package com.lumeneditor.www.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 시 호출되는 메서드입니다.
     * 이 메서드는 사용자가 유효한 자격 증명 없이 보호된 리소스에 접근하려고 할 때 실행됩니다.
     * 이 경우, HTTP 401 Unauthorized 오류를 응답으로 보내어 사용자 인증이 실패했음을 클라이언트에 알립니다.
     *
     * @param request 현재 HTTP 요청 객체입니다. 요청에 대한 세부 정보를 포함하고 있습니다.
     * @param response 현재 HTTP 응답 객체입니다. 이 객체를 사용하여 클라이언트에 상태 코드를 전송합니다.
     * @param authException 인증 실패와 관련된 예외 객체입니다. 이 예외는 유효한 자격 증명 없이 접근하려는 시도가 있을 때 발생합니다.
     * @throws IOException 입출력 작업 중 예외가 발생할 경우 발생합니다. 이는 클라이언트에 오류 코드를 보내는 과정에서 발생할 수 있습니다.
     */

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}