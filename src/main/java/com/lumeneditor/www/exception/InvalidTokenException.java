package com.lumeneditor.www.exception;

import org.springframework.security.core.AuthenticationException;


public class InvalidTokenException extends AuthenticationException {


    /**
     * InvalidTokenException을 생성하는 기본 생성자입니다.
     * 이 생성자는 유효하지 않은 토큰에 대한 예외를 표현할 때 사용됩니다.
     * 예외 메시지만을 인자로 받아, 상세한 예외 상황을 설명할 수 있습니다.
     *
     * @param msg 예외 상황을 설명하는 문자열입니다. 예외가 발생한 원인이나 상황을 명시할 수 있습니다.
     */

    public InvalidTokenException(String msg) {
        super(msg);
    }

    /**
     * InvalidTokenException을 생성하는 확장된 생성자입니다.
     * 이 생성자는 유효하지 않은 토큰에 대한 예외와 그 원인이 되는 Throwable을 함께 받습니다.
     * 예외 메시지와 함께 예외의 원인이 되는 다른 예외를 체이닝할 때 사용됩니다.
     *
     * @param msg 예외 상황을 설명하는 문자열입니다. 이 메시지는 예외가 발생한 구체적인 원인을 명시할 수 있습니다.
     * @param cause 원인이 되는 Throwable 객체입니다. 이는 현재 예외의 발생 원인을 나타내는 데 사용됩니다.
     */

    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}