package com.moa.controller;

import com.moa.dto.possible.PossibleTimeRequest;
import com.moa.dto.possible.PossibleTimeResponse;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.PossibleTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/recruitment/{recruitmentId}/time")
@Controller
public class PossibleTimeController {
    private final PossibleTimeService possibleTimeService;

    @GetMapping("/all")
    public List<PossibleTimeResponse> getAllMembersTimeList(@PathVariable Long recruitmentId, @AuthenticationPrincipal JwtUser user) {
        return possibleTimeService.getAllMembersTimeList(recruitmentId);
    }

    @GetMapping
    public PossibleTimeResponse getTimeList(@PathVariable Long recruitmentId, @AuthenticationPrincipal JwtUser user) {
        return possibleTimeService.getTimeList(recruitmentId, user.id());
    }

    @PutMapping
    public PossibleTimeResponse setPossibleTime(@RequestBody @Valid PossibleTimeRequest timeRequest,
                                                @PathVariable Long recruitmentId,
                                                @AuthenticationPrincipal JwtUser user) {
        possibleTimeService.setTime(timeRequest, recruitmentId, user.id());
        return possibleTimeService.getTimeList(recruitmentId, user.id());
    }
}
