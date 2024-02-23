package com.lumeneditor.www.config;


import com.lumeneditor.www.domain.auth.email.EmailAuthRepository;
import com.lumeneditor.www.domain.auth.email.EmailService;
import com.lumeneditor.www.domain.auth.email.EmailServiceImpl;
import com.lumeneditor.www.domain.auth.*;
import com.lumeneditor.www.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@ComponentScan(basePackages = "com.lumeneditor.www")
@RequiredArgsConstructor
public class AppConfig {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender javaMailSender;

    // JWT Key
    @Value("${jwt.secret}")
    private String secretKey;

    // MemberService 빈 정의
    @Bean
    public MemberService memberService(AuthenticationManagerBuilder authManagerBuilder, JwtTokenProvider jwtTokenProvider) {
        return new MemberServiceImpl(authManagerBuilder, jwtTokenProvider());
    }

    // JwtTokenProvider 빈 정의
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(redisTemplate, secretKey);
    }

    // AuthService 빈 정의
    @Bean
    public AuthService authService(JwtTokenProvider jwtTokenProvider, AuthRepository authRepository, EmailAuthRepository emailAuthRepository, EmailService emailService) {
        return new AuthServiceImpl(redisTemplate, jwtTokenProvider, authRepository, emailAuthRepository, emailService);
    }

    // EmailService 빈 정의
    @Bean
    public EmailService emailService() {
        return new EmailServiceImpl(javaMailSender);
    }

}
