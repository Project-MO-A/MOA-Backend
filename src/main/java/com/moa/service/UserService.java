package com.moa.service;

import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.user.UserSignupRequest;
import com.moa.global.auth.model.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUser(userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 유저를 찾을 수 없습니다")));
    }

    public String saveUser(UserSignupRequest request) {
        User user = request.toEntity();
        user.encodePassword(passwordEncoder);
        return userRepository.save(user).getEmail();
    }


}
