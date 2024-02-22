package com.lumeneditor.www.domain.auth;

public interface AuthService {

    /**
     * 액세스 토큰을 기반으로 사용자 로그아웃 처리를 수행합니다.
     * 이 메서드는 액세스 토큰을 파라미터로 받아와서, 해당 토큰에 연결된 사용자의 정보를 추출합니다.
     * 추출된 사용자 ID를 사용하여 Redis에서 해당 사용자의 리프레시 토큰을 조회하고,
     * 리프레시 토큰이 존재할 경우 Redis에서 해당 키를 삭제합니다.
     * 이로써 사용자는 더 이상 해당 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받을 수 없게 됩니다.
     * 추가적인 로그아웃 관련 처리가 필요할 경우, 이 메서드 내에서 수행할 수 있습니다.
     *
     * @param accessToken 로그아웃하려는 사용자의 액세스 토큰. 사용자의 식별 정보를 추출하기 위해 사용됩니다.
     */
    void logout(String accessToken);

}
