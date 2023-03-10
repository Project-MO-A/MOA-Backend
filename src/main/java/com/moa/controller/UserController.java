package com.moa.controller;

import com.moa.dto.user.UserSignupRequest;
import com.moa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody @Valid UserSignupRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(userService.saveUser(request));
    }

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> signOut(@RequestParam String email){
        userService.deleteUser(email);
        return new ResponseEntity<>(OK);
    }
}
