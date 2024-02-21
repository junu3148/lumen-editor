package com.lumeneditor.www.web.dto.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefreshToken {
    private String username;  // 사용자 이름 또는 ID
    private int role;
    private String refreshToken;     // 리프레시 토큰 값
    private Date expiryDate;  // 토큰의 만료 시간
}