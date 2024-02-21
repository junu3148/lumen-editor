package com.lumeneditor.www.domain.auth;

import com.lumeneditor.www.domain.auth.AuthRepository;
import com.lumeneditor.www.web.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자 정보 조회
        User user = authRepository.findByUserId(userId);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + userId);
        }

        return createUserDetails(user);
    }


    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(User user) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // int 타입의 role을 String으로 변환
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }

}
