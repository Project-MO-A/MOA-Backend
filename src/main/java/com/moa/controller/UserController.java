package com.moa.controller;

import com.moa.dto.user.*;
import com.moa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> signOut(@RequestParam String email){
        userService.deleteUser(email);
        return new ResponseEntity<>(OK);
    }
    
    @GetMapping("/info")
    public UserInfoResponse getInfo(@RequestParam String email) {
        return userService.getUserInfoByEmail(email);
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
}
