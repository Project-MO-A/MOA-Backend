package com.moa.service;

import com.moa.domain.member.ApplimentMember;
import com.moa.domain.member.ApplimentMemberRepository;
import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.domain.user.User;
import com.moa.domain.user.UserRepository;
import com.moa.dto.user.UserInfoUpdateRequest;
import com.moa.dto.user.UserProfileUpdateRequest;
import com.moa.dto.recruit.RecruitmentsInfo;
import com.moa.dto.user.*;
import com.moa.global.auth.model.SecurityUser;
import com.moa.global.exception.BusinessException;
import com.moa.global.exception.service.EntityNotFoundException;
import com.moa.service.util.S3Accessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.moa.global.exception.ErrorCode.*;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final ApplimentMemberRepository applimentMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Accessor s3Accessor;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUser(userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.getCode() + " " + USER_NOT_FOUND.getMessageCode())));
    }

    public UserEmailResponse saveUser(UserSignupRequest request) {
        Optional<User> findUser = userRepository.findByEmail(request.email());
        if (findUser.isPresent()) {
            throw new BusinessException(USER_DUPLICATED_EMAIL);
        }
        User user = request.toEntity();
        user.encodePassword(passwordEncoder);
        user.addInterests(request.interestsValue());
        return new UserEmailResponse(userRepository.save(user).getEmail());
    }

    @Transactional(readOnly = true)
    public UserInfo getUserProfileInfoById(final Long userId) {
        User user = findUser(userId);
        String imageUrl = null;
        if (user.getImageUrl() != null) {
            imageUrl = s3Accessor.load(user.getImageUrl());
        }
        return new UserInfo(user, imageUrl);
    }

    @Transactional(readOnly = true)
    public RecruitmentsInfo getUserWritingInfoById(final Long userId) {
        List<Recruitment> recruitments = recruitmentRepository.findFetchTagsByUserId(userId);
        return new RecruitmentsInfo(recruitments);
    }

    @Transactional(readOnly = true)
    public UserActivityInfo getUserActivityInfoById(Long userId) {
        List<ApplimentMember> applimentMembers = applimentMemberRepository.findAllRecruitmentByUserId(userId);
        return new UserActivityInfo(applimentMembers);
    }

    @Transactional(readOnly = true)
    public UserRecruitmentInterestInfo getUserConcernInfoById(final Long userId) {
        User user = userRepository.findRecruitmentInterestById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        return new UserRecruitmentInterestInfo(user);
    }

    public void deleteUser(Long id) {
        userRepository.delete(findUser(id));
    }

    public void updateUser(final UserUpdateRequest updateRequest) {
        User user = userRepository.findByEmail(updateRequest.email())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        user.update(updateRequest);
    }

    public Long updateUserProfile(UserProfileUpdateRequest request, Long userId) {
        User user = findUser(userId);
        user.update(request);
        return user.getId();
    }

    public Long updateUserInfo(UserInfoUpdateRequest request, MultipartFile image, Long userId) {
        User user = findUser(userId);
        String imageUrl = null;
        if (image != null) {
            imageUrl = s3Accessor.save(image, String.valueOf(user.getId()));
        }
        user.update(request, imageUrl, passwordEncoder);
        return user.getId();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    public void changePassword(final UserPwUpdateRequest pwUpdateRequest) {
        User user = userRepository.findByEmail(pwUpdateRequest.email())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        validatePassword(user.getPassword(), pwUpdateRequest.currentPassword());
        user.changePassword(passwordEncoder, pwUpdateRequest.newPassword());
    }

    @Transactional(readOnly = true)
    public Boolean checkEmailUnique(final String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    private void validatePassword(final String userPassword, final String givenPassword) {
        if (!passwordEncoder.matches(givenPassword, userPassword)) throw new BusinessException(USER_MISMATCH_PASSWORD);
    }
}
