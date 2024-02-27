package com.lumeneditor.www.domain.auth.entity;

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

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userPassword;

    @Column(nullable = false)
    private String userName;

    private String phoneNumber;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date accessionDate;

    @Temporal(TemporalType.DATE)
    private Date withdrawalDate;

    private Integer birthYear;
    private String occupation;
    private String country;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.M; // 예시 기본값 설정

    @Enumerated(EnumType.STRING)
    private YesNo emailAccept = YesNo.N; // 기본값 설정

    @Enumerated(EnumType.STRING)
    private YesNo promoAccept = YesNo.N; // 기본값 설정

    @Enumerated(EnumType.STRING)
    private YesNo userStatus = YesNo.Y; // 기본값 설정

    private Integer outInfo = 0;
    private Integer subRound = 0;
    private String company;
    private Integer isDeleted = 0;
    private String logoImage;

    private Long planKey;

    @Temporal(TemporalType.DATE)
    private Date passwordRecovery;

    private String role;

    @PrePersist
    protected void onCreate() {
        accessionDate = new Date();
        passwordRecovery = new Date();
    }

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

    public String getUserName() {
        return userName;
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
