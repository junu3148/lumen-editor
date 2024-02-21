package com.lumeneditor.www.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * InvalidTokenException 클래스는 인증 과정에서 토큰이 유효하지 않을 때 발생하는 예외를 표현합니다.
 * 이 클래스는 AuthenticationException을 확장하여, 인증 과정 중 특정한 오류 상황을 더 명확하게 구분할 수 있도록 합니다.
 */
public class InvalidTokenException extends AuthenticationException {

    /**
     * 예외 메시지를 받아 이를 상위 클래스인 AuthenticationException에 전달하는 기본 생성자입니다.
     *
     * @param msg 발생한 예외에 대한 설명을 포함하는 문자열입니다.
     */
    public InvalidTokenException(String msg) {
        super(msg);
    }

    /**
     * 예외 메시지와 원인을 나타내는 Throwable 객체를 받아 상위 클래스에 전달하는 생성자입니다.
     * 이 생성자는 예외가 발생한 근본적인 원인을 함께 제공할 수 있도록 합니다.
     *
     * @param msg   발생한 예외에 대한 설명을 포함하는 문자열입니다.
     * @param cause 예외의 원인을 나타내는 Throwable 객체입니다.
     */
    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}