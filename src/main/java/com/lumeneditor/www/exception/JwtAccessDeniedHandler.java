package com.lumeneditor.www.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 접근 권한이 없는 요청에 대한 핸들러 메서드입니다.
     * 사용자가 필요한 권한 없이 보호된 리소스에 접근하려고 할 때 이 메서드가 호출됩니다.
     * 이 경우, HTTP 403 Forbidden 오류를 응답으로 보내어 접근이 금지되었음을 클라이언트에 알립니다.
     *
     * @param request 현재 HTTP 요청 객체입니다. 요청에 대한 세부 정보를 포함하고 있습니다.
     * @param response 현재 HTTP 응답 객체입니다. 이 객체를 사용하여 클라이언트에 상태 코드를 전송합니다.
     * @param accessDeniedException 접근 거부 예외 객체입니다. 이 예외는 필요한 권한 없이 접근하려는 시도가 있을 때 발생합니다.
     * @throws IOException 입출력 작업 중 예외가 발생할 경우 발생합니다.
     */

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        //필요한 권한이 없이 접근하려 할때 403
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

}