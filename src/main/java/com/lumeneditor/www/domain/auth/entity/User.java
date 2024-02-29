package com.lumeneditor.www.domain.auth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public User(String userId, String userPassword,String role) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.role = role;
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
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private YesNo emailAccept;

    @Enumerated(EnumType.STRING)
    private YesNo promoAccept;

    @Enumerated(EnumType.STRING)
    private YesNo userStatus;

    private Integer outInfo;
    private Integer subRound;
    private String company;
    private Integer isDeleted;
    private String logoImage;

    private Long planKey;

    @Temporal(TemporalType.DATE)
    private Date passwordRecovery;

    private String role;

    @PrePersist
    protected void onCreate() {
        accessionDate = new Date();
        emailAccept = YesNo.N;
        promoAccept = YesNo.N;
        userStatus = YesNo.Y;
        outInfo = 0;
        subRound = 0;
        isDeleted = 0;
        passwordRecovery = new Date();
        planKey = 1L;
        role = "User";
    }

    @PreUpdate
    protected void onUpdate() {
        withdrawalDate = new Date(); // 탈퇴시
        passwordRecovery = new Date(); // 비밀번호 변경시
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


    @JsonProperty("userName")
    public String getFullName() {
        return userName;
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
