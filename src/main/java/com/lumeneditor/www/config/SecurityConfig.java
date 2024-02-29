package com.lumeneditor.www.config;

import com.lumeneditor.www.exception.JwtAccessDeniedHandler;
import com.lumeneditor.www.exception.JwtAuthenticationEntryPoint;
import com.lumeneditor.www.security.JwtAuthenticationFilter;
import com.lumeneditor.www.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈을 등록합니다.
     * BCryptPasswordEncoder는 BCrypt 해싱 함수를 사용하여 비밀번호를 암호화하는 구현체입니다.
     * 이 방식은 안전한 비밀번호 저장을 위한 업계 표준 중 하나입니다.
     *
     * @return BCryptPasswordEncoder 인스턴스를 반환합니다.
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 필터 체인을 구성합니다.
     * 이 메서드는 애플리케이션의 보안 요구 사항에 따라 HTTP 보안 설정을 정의합니다.
     * <p>
     * 설정 내용:
     * - HTTP 기본 인증을 비활성화하여, 사용자 인증을 위해 자체 구현한 방식을 사용합니다.
     * - CSRF 보호 기능을 비활성화하여, REST API가 상태가 없는(stateless) 특성을 유지할 수 있도록 합니다.
     * - 세션을 생성하지 않는 상태가 없는(stateless) 세션 관리를 설정합니다. 이는 JWT와 같은 토큰 기반 인증에 적합합니다.
     * - 모든 오리진에서의 요청을 허용하는 기본 CORS 설정을 활성화합니다.
     * - 특정 경로에 대한 접근 권한을 설정하여, 일부 경로는 인증 없이 접근할 수 있도록 합니다.
     * - 인증 실패 또는 접근 거부 시 처리를 위한 핸들러를 설정합니다.
     * - JwtAuthenticationFilter를 추가하여, 모든 요청에 대해 JWT 검증을 수행합니다.
     *
     * @param http HttpSecurity 객체를 통해 웹 보안 설정을 구성합니다.
     * @return 구성된 SecurityFilterChain 객체를 반환합니다.
     * @throws Exception 보안 구성 과정에서 발생할 수 있는 예외를 처리합니다.
     */

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
        http.authorizeHttpRequests(authz -> authz
                // "/auth/login" 엔드포인트에 대한 접근은 모든 사용자에게 허용
                .requestMatchers("/auth/login", "/auth/signup", "/auth/send-auth-code", "auth/verify", "auth/access-token").permitAll()
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
