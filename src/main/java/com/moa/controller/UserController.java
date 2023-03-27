package com.moa.controller;

import com.moa.dto.user.*;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserEmailResponse> signUp(@RequestBody @Valid UserSignupRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(userService.saveUser(request));
    }

    @ResponseStatus(OK)
    @DeleteMapping("/sign-out")
    public void signOut(@AuthenticationPrincipal JwtUser user){
        userService.deleteUser(user.id());
    }
    
    @GetMapping("/info")
    public UserInfoResponse getInfo(@AuthenticationPrincipal JwtUser user) {
        return userService.getUserInfoById(user.id());
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
    public Boolean checkEmailUnique(@RequestBody @Valid EmailDuplicateRequest request) {
        return userService.checkEmailUnique(request.email());
    }
}
