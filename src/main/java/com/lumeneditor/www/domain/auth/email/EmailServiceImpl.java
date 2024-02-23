package com.lumeneditor.www.domain.auth.email;

import com.lumeneditor.www.exception.CustomException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public ResponseEntity<Boolean> sendAuthenticationCodeEmail(String email,String code) {

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("LUMEN 비밀번호 인증")
                .message(code)
                .build();

        try {
            // 모든 수신자에 대해 이메일을 비동기적으로 발송
            sendMailAsync(emailMessage, email);
            // 이메일 발송이 성공적으로 완료되면 true를 ResponseEntity로 감싸서 반환
            return new ResponseEntity<>(true, HttpStatus.OK);

        } catch (MessagingException e) {
            // MessagingException 포함 모든 예외를 여기서 처리
            // 예외 발생 시 CustomException을 던지는 대신, 실패 응답 반환
            throw new CustomException("Failed to send email", e);
        }
    }


    // 메시지 객체 생성
    private void sendMailAsync(EmailMessage emailMessage, String recipient) throws MessagingException {
        // JavaMailSender를 사용하여 MIME 메시지를 생성합니다.
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(sender); // 발신자 설정
        mimeMessageHelper.setTo(recipient); // 개별 수신자 설정
        mimeMessageHelper.setSubject(emailMessage.getSubject()); // 제목 설정
        mimeMessageHelper.setText(emailMessage.getMessage(), true); // 메시지 본문 설정, HTML 사용 여부는 true 또는 false로


        javaMailSender.send(mimeMessage); // 메일 발송
    }





}


