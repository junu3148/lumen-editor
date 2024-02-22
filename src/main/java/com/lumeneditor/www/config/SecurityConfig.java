package com.lumeneditor.www.config;

import com.lumeneditor.www.exception.JwtAccessDeniedHandler;
import com.lumeneditor.www.exception.JwtAuthenticationEntryPoint;
import com.lumeneditor.www.security.JwtAuthenticationFilter;
import com.lumeneditor.www.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // PasswordEncoder는 BCryptPasswordEncoder를 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // HTTP 기본 인증 비활성화
        http.httpBasic(HttpBasicConfigurer::disable);

        // CSRF(Cross-Site Request Forgery) 보호 기능 비활성화
        http.csrf(CsrfConfigurer::disable);

        // 세션 관리를 상태가 없는(Stateless) 방식으로 설정하여 세션 생성 방지
        http.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 기본 설정으로 CORS(Cross-Origin Resource Sharing) 활성화
        http.cors(Customizer.withDefaults());

        // HTTP 요청 권한 부여 설정
        http.authorizeHttpRequests((authz) -> authz
                // "/auth/login" 엔드포인트에 대한 접근은 모든 사용자에게 허용
                .requestMatchers("/auth/login").permitAll()
                // "/" 루트 경로에 대한 접근은 모든 사용자에게 허용
                .requestMatchers("/").permitAll()
                // 나머지 모든 요청에 대한 접근은 인증된 사용자에게만 허용
                .anyRequest().authenticated());

        // 예외 처리 설정
        http.exceptionHandling(authenticationManager -> authenticationManager
                // JWT 인증 진입점 설정
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // JWT 접근 거부 핸들러 설정
                .accessDeniedHandler(jwtAccessDeniedHandler));

        // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 이전에 추가
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



}
