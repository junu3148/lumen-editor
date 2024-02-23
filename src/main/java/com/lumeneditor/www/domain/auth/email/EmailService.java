package com.lumeneditor.www.domain.auth.email;

import org.springframework.http.ResponseEntity;

public interface EmailService {

    /**
     * 주어진 이메일 메시지를 특정 수신자들에게 비동기적으로 발송합니다.
     * <p>
     * 이 메서드는 정의된 수신자 목록에 대해 각각 비동기 이메일 발송 메서드(sendMailAsync)를 호출합니다.
     * 이메일 전송이 성공하면 콘솔에 서비스 시간을 출력하고, 실패하면 로그에 오류를 기록하고 RuntimeException을 발생시킵니다.
     * 모든 이메일 전송 시도 후에는 "ok" 문자열을 반환하여 전송이 시작되었음을 나타냅니다.
     *
     * @param email 이메일 발송에 사용할 프로모션 데이터 객체
     * @return 이메일 전송이 시작되었음을 나타내는 "ok" 문자열.
     * @throws RuntimeException 이메일 전송 중 예외가 발생한 경우.
     */

    ResponseEntity<Boolean> sendAuthenticationCodeEmail(String email, String code);




}