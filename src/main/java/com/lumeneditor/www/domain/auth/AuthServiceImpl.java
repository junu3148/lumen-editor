package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그아웃
    @Override
    public void logout(String accessToken) {

        String userId = jwtTokenProvider.getAdminUserInfoFromToken(accessToken);
        // Redis에서 사용자 ID를 키로 사용하여 refresh token 조회
        String refreshToken = redisTemplate.opsForValue().get(userId);
        if (refreshToken != null) {
            // Redis에서 refresh token 삭제
            redisTemplate.delete(userId);
        }
        // 필요한 경우 추가 작업 수행 (예: 데이터베이스 상태 업데이트, 로깅 등)
    }


}
