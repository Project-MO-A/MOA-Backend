package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/link")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping
    public ValueResponse<Long> addLink(@AuthenticationPrincipal JwtUser user, @RequestParam(name = "uri") String uri){
        return new ValueResponse<>(linkService.addLink(user.id(), uri));
    }
}
