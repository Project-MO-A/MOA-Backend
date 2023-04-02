package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.dto.recruit.RecruitmentsInfo;
import com.moa.dto.user.*;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @ResponseStatus(CREATED)
    @PostMapping("/sign-up")
    public UserEmailResponse signUp(@RequestBody @Valid UserSignupRequest request) {
        return userService.saveUser(request);
    }

    @ResponseStatus(OK)
    @DeleteMapping("/sign-out")
    public void signOut(@AuthenticationPrincipal JwtUser user){
        userService.deleteUser(user.id());
    }
    
    @GetMapping("/info/profile")
    public UserInfo getProfileInfo(@AuthenticationPrincipal JwtUser user) {
        return userService.getUserProfileInfoById(user.id());
    }

    @GetMapping("/info/writing")
    public RecruitmentsInfo getWritingInfo(@AuthenticationPrincipal JwtUser user) {
        return userService.getUserWritingInfoById(user.id());
    }

    @GetMapping("/info/activity")
    public UserActivityInfo getActivityInfo(@AuthenticationPrincipal JwtUser user) {
        return userService.getUserActivityInfoById(user.id());
    }

    @GetMapping("/info/concern")
    public UserRecruitmentInterestInfo getConcernInfo(@AuthenticationPrincipal JwtUser user) {
        return userService.getUserConcernInfoById(user.id());
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping("/info")
    public void update(@RequestBody @Valid UserUpdateRequest updateRequest) {
        userService.updateUser(updateRequest);
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping("/pw")
    public void changePassword(@RequestBody @Valid UserPwUpdateRequest pwUpdateRequest) {
        userService.changePassword(pwUpdateRequest);
    }

    @GetMapping("/check/email")
    public ValueResponse<Boolean> checkEmailUnique(@RequestBody @Valid EmailDuplicateRequest request) {
        return new ValueResponse<>(userService.checkEmailUnique(request.email()));
    }
}
