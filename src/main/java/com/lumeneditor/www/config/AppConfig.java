package com.lumeneditor.www.config;


import com.lumeneditor.www.security.JwtTokenProvider;
import com.lumeneditor.www.domain.auth.MemberService;
import com.lumeneditor.www.domain.auth.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@ComponentScan(basePackages = "com.lumeneditor.www")
public class AppConfig {
/*

    private final SqlSession sqlSession;

    public AppConfig(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
*/

    @Value("${jwt.secret}")
    private String secretKey;


    @Bean
    public MemberService memberService(AuthenticationManagerBuilder authManagerBuilder, JwtTokenProvider jwtTokenProvider) {
        return new MemberServiceImpl(authManagerBuilder, jwtTokenProvider());
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(secretKey);
    }




}
