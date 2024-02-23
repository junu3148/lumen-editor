package com.lumeneditor.www.web.dto;

import com.lumeneditor.www.comm.eunm.Gender;
import com.lumeneditor.www.comm.eunm.YesNo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    public User(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userKey;

    @Column(unique = true)
    private String userId;

    private String userPassword;
    private String userName;
    private String phoneNumber;

    @Temporal(TemporalType.DATE) // 날짜 타입으로 지정
    private Date accessionDate;

    @Temporal(TemporalType.DATE) // 날짜 타입으로 지정
    private Date withdrawalDate;

    private Integer birthYear; // 필드 타입을 기본형에서 래퍼 클래스로 변경
    private String occupation;
    private String country;

    @Enumerated(EnumType.STRING) // enum 타입을 문자열로 저장
    private Gender gender; // Gender enum 타입 사용

    @Enumerated(EnumType.STRING)
    private YesNo emailAccept; // YesNo enum 타입 사용

    @Enumerated(EnumType.STRING)
    private YesNo promoAccept;

    @Enumerated(EnumType.STRING)
    private YesNo userStatus;

    private Integer outInfo;
    private Integer subRound;
    private String company;
    private Integer isDeleted; // 필드 타입 변경
    private String logoImage;

    private Long planKey;

    @Temporal(TemporalType.DATE)
    private Date passwordRecovery; // password_recovery 필드 추가

    // 단일 권한 필드로 변경
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (this.role != null && !this.role.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(this.role));
        }
        return authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
