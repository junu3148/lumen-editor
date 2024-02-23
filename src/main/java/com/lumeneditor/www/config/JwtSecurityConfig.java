package com.lumeneditor.www.config;

import com.lumeneditor.www.security.JwtAuthenticationFilter;
import com.lumeneditor.www.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Spring Security 설정을 구성하는 메서드입니다.
     * 이 메서드는 HttpSecurity 객체를 사용하여 애플리케이션의 보안 관련 설정을 정의합니다.
     * 특히, JWT 인증 필터(JwtAuthenticationFilter)를 Spring Security 필터 체인에 등록합니다.
     * 이 필터는 사용자가 요청한 자원에 접근하기 전에 실행되며, 사용자의 JWT 토큰을 검증합니다.
     *
     * JwtAuthenticationFilter는 기존의 UsernamePasswordAuthenticationFilter 전에 추가되어,
     * 요청이 처리되기 전에 사용자의 인증 상태를 확인할 수 있도록 합니다.
     * 이를 통해 JWT 기반의 인증 메커니즘을 애플리케이션에 통합할 수 있습니다.
     *
     * @param http HttpSecurity 객체를 통해 웹 보안 설정을 구성합니다.
     */

    @Override
    public void configure(HttpSecurity http) {

        // security 로직에 JwtFilter 등록
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
