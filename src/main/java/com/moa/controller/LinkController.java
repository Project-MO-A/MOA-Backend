package com.moa.controller;

import com.moa.dto.ValueResponse;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/user/link")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @ResponseStatus(CREATED)
    @PostMapping
    public ValueResponse<Long> addLink(@AuthenticationPrincipal JwtUser user, @RequestParam(name = "uri") String uri){
        return new ValueResponse<>(linkService.addLink(user.id(), uri));
    }
}
