package com.moa.controller;

import com.moa.dto.possible.PossibleTimeRequest;
import com.moa.dto.possible.PossibleTimeResponse;
import com.moa.global.auth.model.JwtUser;
import com.moa.service.PossibleTimeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@RequestMapping("/recruitment/{recruitmentId}/time")
@RestController
public class PossibleTimeController {
    private final PossibleTimeService possibleTimeService;

    @GetMapping("/all")
    public List<PossibleTimeResponse> getAllMembersTimeList(@PathVariable Long recruitmentId) {
        return possibleTimeService.getAllMembersTimeList(recruitmentId);
    }

    @GetMapping
    public List<LocalDateTime> getTimeList(@PathVariable Long recruitmentId, @AuthenticationPrincipal JwtUser user) {
        return possibleTimeService.getTimeList(recruitmentId, user.id());
    }

    @ResponseStatus(NO_CONTENT)
    @PutMapping
    public void setPossibleTime(@RequestBody @Valid PossibleTimeRequest timeRequest,
                                                @PathVariable Long recruitmentId,
                                                @AuthenticationPrincipal JwtUser user,
                                                HttpServletResponse response) throws IOException {
        possibleTimeService.setTime(timeRequest, recruitmentId, user.id());
        response.sendRedirect(String.format("/recruitment/%d/time", recruitmentId));
    }
}
