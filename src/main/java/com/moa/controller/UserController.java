package com.moa.controller;

import com.moa.dto.user.*;
import com.moa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("sign-up")
    public ResponseEntity<String> signUp(@RequestBody @Valid UserSignupRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(userService.saveUser(request));
    }

    @GetMapping("info")
    public UserInfoResponse getInfo(@Login UserLogin user) {
        return userService.getUserInfoByEmail(user.email());
    }

    @PatchMapping("info")
    public String update(@RequestBody @Valid UserUpdateRequest updateRequest, @Login UserLogin user) {
        return userService.updateUser(updateRequest, user.email());
    }
}
