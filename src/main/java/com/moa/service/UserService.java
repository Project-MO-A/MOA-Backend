package com.moa.service;

import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.user.*;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.exception.auth.WrongPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUser(userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다")));
    }

    public UserEmailResponse saveUser(UserSignupRequest request) {
        Optional<User> findUser = userRepository.findByEmail(request.email());
        if (findUser.isPresent()) {
            throw new DuplicateKeyException("해당 이메일로 가입이 불가능합니다");
        }
        User user = request.toEntity();
        user.encodePassword(passwordEncoder);
        user.addInterests(request.interestsValue());
        return new UserEmailResponse(userRepository.save(user).getEmail());
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfoByEmail(final String email) {
        return new UserInfoResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다")));
    }

    public void deleteUser(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        if (findUser.isEmpty()) {
            throw new UsernameNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다");
        }
        userRepository.delete(findUser.get());
    }
    
    public void updateUser(final UserUpdateRequest updateRequest) {
        User user = userRepository.findByEmail(updateRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다"));
        user.update(updateRequest);
    }

    public void changePassword(final UserPwUpdateRequest pwUpdateRequest) {
        User user = userRepository.findByEmail(pwUpdateRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다"));
        validatePassword(user.getPassword(), pwUpdateRequest.currentPassword());
        user.changePassword(passwordEncoder, pwUpdateRequest.newPassword());
    }

    private void validatePassword(final String userPassword, final String givenPassword) {
        if (!passwordEncoder.matches(givenPassword, userPassword)) throw new WrongPasswordException();
    }
}
