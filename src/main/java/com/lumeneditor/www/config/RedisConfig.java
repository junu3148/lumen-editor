package com.lumeneditor.www.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * Redis 연결 팩토리를 생성하는 메서드입니다.
     * 이 메서드는 Spring Data Redis와 Lettuce 클라이언트를 사용하여
     * Redis 서버에 연결하기 위한 연결 팩토리를 설정하고 생성합니다.
     * RedisStandaloneConfiguration을 사용하여 Redis 서버의 호스트 이름과 포트를 설정합니다.
     * 이 설정은 단일 노드 Redis 환경에 대한 기본 연결 설정을 제공합니다.
     *
     * @return LettuceConnectionFactory를 사용하여 생성된 RedisConnectionFactory 객체입니다.
     *         이 객체는 Redis 서버와의 연결을 관리하며, RedisTemplate에 의해 사용됩니다.
     */

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        return new LettuceConnectionFactory(config);
    }

    /**
     * 애플리케이션에서 Redis 데이터 작업을 수행하기 위한 RedisTemplate 인스턴스를 생성합니다.
     * 이 메서드는 Redis와의 데이터 교환을 위한 템플릿을 제공합니다. RedisTemplate은
     * String 타입의 키와 값에 대한 Redis 연산을 캡슐화합니다.
     * RedisConnectionFactory를 사용하여 Redis 서버와의 연결을 설정합니다.
     *
     * @return Redis 작업을 위해 구성된 RedisTemplate<String, String> 인스턴스입니다.
     *         이 템플릿을 사용하여 Redis 서버에 데이터를 저장하거나 조회할 수 있습니다.
     */

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }

}
