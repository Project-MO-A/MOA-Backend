package com.moa.global.filter.handler;

import com.moa.domain.recruit.Recruitment;
import com.moa.domain.recruit.RecruitmentRepository;
import com.moa.global.auth.model.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class RecruitmentAuthorizationManagerForAuthor implements AuthorizationManager<RequestAuthorizationContext> {

    private final RecruitmentRepository recruitmentRepository;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        JwtUser user = (JwtUser) authentication.get().getPrincipal();
        String requestURI = context.getRequest().getRequestURI();
        return new AuthorizationDecision(canAccess(user, requestURI));
    }

    private boolean canAccess(JwtUser user, String requestURI) {
        return recruitmentRepository.findAllFetchUserById(user.id()).stream()
                .map(Recruitment::getId)
                .anyMatch(recruitmentId -> match(recruitmentId, requestURI));
    }

    private boolean match(Long recruitmentId, String requestURI) {
        String[] parsingUri = requestURI.split("/");
        return parsingUri[2].equals(String.valueOf(recruitmentId));
    }
}
