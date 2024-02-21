package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.web.dto.User;
import com.lumeneditor.www.web.dto.auth.JwtToken;
import org.springframework.http.ResponseEntity;


public interface MemberService {

    /**
     * 관리자 사용자로 로그인하고, JWT 토큰을 생성하여 반환합니다.
     * <p>
     * 이 메서드는 입력받은 관리자 사용자 정보({@code AdminUser})를 사용하여 로그인을 시도합니다.
     * 로그인이 성공적으로 완료되면, 해당 사용자에 대한 JWT 토큰을 생성하고, 이를 {@code ResponseEntity<JwtToken>} 형태로
     * 감싸서 반환합니다. 로그인이나 토큰 생성 과정에서 문제가 발생한 경우, 적절한 오류 메시지와 함께 응답이 반환될 수 있습니다.
     *
     * @param user 로그인을 시도할 관리자 사용자의 정보를 담고 있는 {@code AdminUser} 객체.
     * @return 생성된 JWT 토큰을 담고 있는 {@code ResponseEntity<JwtToken>} 객체. 성공적으로 토큰이 생성되면,
     * {@code HttpStatus.OK} 상태와 함께 토큰이 반환되며, 실패한 경우 적절한 HTTP 오류 상태와 오류 메시지가 반환됩니다.
     */

    ResponseEntity<JwtToken> signInAndGenerateJwtToken(User user);

    /**
     * 제공된 리프레시 토큰을 검증하고, 새로운 JWT 토큰을 생성하여 반환합니다.
     * <p>
     * 이 메서드는 입력받은 리프레시 토큰({@code String})이 유효한지 검증합니다. 토큰이 유효한 경우,
     * 새로운 JWT 액세스 토큰을 생성하고 이를 {@code ResponseEntity<String>} 형태로 감싸서 반환합니다.
     * 리프레시 토큰이 유효하지 않거나 만료된 경우, 적절한 오류 메시지와 함께 오류 응답이 반환됩니다.
     *
     * @param refreshToken 검증하고 새로운 JWT 액세스 토큰을 생성하기 위한 리프레시 토큰 문자열.
     * @return 생성된 새로운 JWT 액세스 토큰을 담고 있는 {@code ResponseEntity<String>} 객체. 리프레시 토큰이
     * 유효한 경우, {@code HttpStatus.OK} 상태와 함께 새 토큰이 반환되며, 실패한 경우 적절한 HTTP 오류 상태와
     * 오류 메시지가 반환됩니다.
     */

   //ResponseEntity<String> refreshTokenCK(String refreshToken);


}