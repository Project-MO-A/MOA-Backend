package com.moa.global.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.moa.global.auth.model.Claims;
import com.moa.global.auth.model.JwtUser;
import com.moa.global.auth.model.TokenMapping;
import com.moa.global.auth.token.AccessTokenProvider;
import com.moa.global.auth.token.RefreshTokenProvider;
import com.moa.global.auth.token.TokenExtractor;
import com.moa.global.auth.token.TokenInjector;
import com.moa.global.exception.auth.BusinessAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.moa.global.auth.utils.AuthorityToStringConvertor.reverseConvert;
import static com.moa.global.exception.ErrorCode.JWT_NOT_VALID;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenExtractor extractor;
    private final TokenInjector injector;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        TokenMapping tokenMapping = extractor.extractToken(request);
        if (tokenMapping.hasNoAccessToken()) {
            filterChain.doFilter(request, response);
            return;
        }
        doFilterInternal(request, response, filterChain, tokenMapping);
    }

    private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, TokenMapping tokens) throws ServletException, IOException {
        boolean reIssued = true;
        if (!accessTokenProvider.valid(tokens.accessToken())) {
            reIssued = reIssueAccessToken(response, tokens);
        }
        if (reIssued) {
            saveAuthentication(tokens.accessToken());
        }
        filterChain.doFilter(request, response);
    }

    private boolean reIssueAccessToken(HttpServletResponse response, TokenMapping tokens) {
        if (tokens.hasNoRefreshToken()) {
            return false;
        }
        Claims claims = refreshTokenProvider.extractClaim(tokens.refreshToken());
        validateEmail(tokens.accessToken(), claims.userId());
        String recreateAccessToken = accessTokenProvider.createToken(claims);
        tokens = new TokenMapping(recreateAccessToken, tokens.refreshToken());
        injector.injectToken(response, tokens);
        return true;
    }

    private void validateEmail(String accessToken, Long userId) {
        try {
            Long tokenUserId = accessTokenProvider.extractClaim(accessToken).userId();
            if (!tokenUserId.equals(userId)) {
                throw new BusinessAuthenticationException(JWT_NOT_VALID);
            }
        } catch (JWTDecodeException exception) {
            throw new BusinessAuthenticationException(JWT_NOT_VALID);
        }
    }

    private void saveAuthentication(String accessToken) {
        Claims claims = accessTokenProvider.extractClaim(accessToken);
        SecurityContextHolder.getContext().setAuthentication(createAuthentication(claims));
    }

    private UsernamePasswordAuthenticationToken createAuthentication(Claims claims) {
        return new UsernamePasswordAuthenticationToken(new JwtUser(claims.userId()), null, reverseConvert(claims.role()));
    }
}