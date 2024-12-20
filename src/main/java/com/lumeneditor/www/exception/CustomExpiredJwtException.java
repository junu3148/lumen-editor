package com.lumeneditor.www.exception;

import io.jsonwebtoken.JwtException;

public class CustomExpiredJwtException extends JwtException {
    public CustomExpiredJwtException(String message) {
        super(message);
    }

    public CustomExpiredJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}