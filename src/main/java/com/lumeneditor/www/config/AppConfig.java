package com.lumeneditor.www.config;


import com.lumeneditor.www.domain.auth.AuthService;
import com.lumeneditor.www.domain.auth.AuthServiceImpl;
import com.lumeneditor.www.domain.auth.MemberService;
import com.lumeneditor.www.domain.auth.MemberServiceImpl;
import com.lumeneditor.www.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@ComponentScan(basePackages = "com.lumeneditor.www")
public class AppConfig {

    private final RedisTemplate<String, String> redisTemplate;

    /*

        private final SqlSession sqlSession;

        public AppConfig(SqlSession sqlSession) {
            this.sqlSession = sqlSession;
        }
    */
    // JWT Key
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    public AppConfig(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // MemberService
    @Bean
    public MemberService memberService(AuthenticationManagerBuilder authManagerBuilder, JwtTokenProvider jwtTokenProvider) {
        return new MemberServiceImpl(authManagerBuilder, jwtTokenProvider());
    }

    // JwtTokenProvider
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(redisTemplate, secretKey);
    }

    // AuthService
    @Bean
    public AuthService authService(JwtTokenProvider jwtTokenProvider) {
        return new AuthServiceImpl(redisTemplate,jwtTokenProvider);
    }


}
