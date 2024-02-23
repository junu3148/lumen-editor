package com.lumeneditor.www.aspect;

import com.lumeneditor.www.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * HTTP 요청 메서드가 지원되지 않을 때의 예외를 처리합니다.
     * 클라이언트가 서버가 인식할 수 없거나 지원하지 않는 HTTP 메서드를 사용했을 때 발생합니다.
     * 예를 들어, GET 메서드만 지원하는 엔드포인트에 POST 요청을 보낼 경우 이 예외가 발생할 수 있습니다.
     *
     * @param ex      발생한 HttpRequestMethodNotSupportedException 예외 인스턴스입니다.
     * @param request 현재 웹 요청에 대한 정보를 담고 있는 WebRequest 객체입니다.
     * @return HTTP 405 상태 코드와 함께, 메서드가 허용되지 않음을 설명하는 메시지를 담은 ResponseEntity 객체를 반환합니다.
     */

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        log.error("HttpRequestMethodNotSupportedException: ", ex);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Method not allowed: " + ex.getMessage());
    }

    /**
     * 접근 권한이 거부되었을 때의 예외를 처리합니다.
     * 사용자가 요구되는 권한을 충족시키지 못하고 자원에 접근하려 할 때 발생합니다.
     * 예를 들어, 특정 역할을 가진 사용자만 접근할 수 있는 API 엔드포인트에 접근하려고 시도할 때 이 예외가 발생할 수 있습니다.
     *
     * @param ex      발생한 AccessDeniedException 예외 인스턴스입니다.
     * @param request 현재 웹 요청에 대한 정보를 담고 있는 WebRequest 객체입니다.
     * @return HTTP 403 상태 코드와 함께, 접근이 거부됨을 설명하는 메시지를 담은 ResponseEntity 객체를 반환합니다.
     */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("AccessDeniedException: ", ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access denied: " + ex.getMessage());
    }

    /**
     * 데이터베이스 접근 중 발생하는 예외를 처리합니다.
     * 데이터베이스 쿼리, 연결 문제 또는 기타 데이터 접근 관련 문제가 발생했을 때 이 예외가 발생할 수 있습니다.
     *
     * @param ex 발생한 DataAccessException 예외 인스턴스입니다.
     * @return HTTP 500 상태 코드와 함께, 데이터베이스 접근 중 오류가 발생했음을 설명하는 메시지를 담은 ResponseEntity 객체를 반환합니다.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException ex) {
        log.error("DataAccessException: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Database access error occurred: " + ex.getMessage());
    }

    /**
     * 예상치 못한 모든 예외를 처리합니다.
     * 이 핸들러는 애플리케이션에서 처리되지 않은 그 외 모든 종류의 예외를 포괄합니다.
     * 이는 애플리케이션의 다른 부분에서 특별히 처리되지 않은 예외들에 대한 일반적인 처리 방법을 제공합니다.
     *
     * @param ex      발생한 Exception 예외 인스턴스입니다.
     * @param request 현재 웹 요청에 대한 정보를 담고 있는 WebRequest 객체입니다.
     * @return HTTP 500 상태 코드와 함께, 예상치 못한 오류가 발생했음을 설명하는 메시지를 담은 ResponseEntity 객체를 반환합니다.
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
    /**
     * 사용자 정의 예외를 처리합니다.
     * 이 핸들러는 애플리케이션에서 정의한 CustomException과 그 하위 유형의 예외를 특별히 처리합니다.
     * 사용자 정의 예외는 특정 비즈니스 로직이나 요구 사항에 맞춰진 예외를 의미합니다.
     *
     * @param ex 발생한 CustomException 예외 인스턴스입니다.
     * @return HTTP 400 상태 코드와 함께, 사용자 정의 오류가 발생했음을 설명하는 메시지를 담은 ResponseEntity 객체를 반환합니다.
     */

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        log.error("CustomException: ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Custom error: " + ex.getMessage());
    }

}