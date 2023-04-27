package com.moa.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_LOG_NO_BODY_FORMAT = "REQUEST :: METHOD: {}, URL: {}";
    private static final String REQUEST_LOG_FORMAT = REQUEST_LOG_NO_BODY_FORMAT + ", BODY: {}";
    private static final String RESPONSE_LOG_NO_BODY_FORMAT = "RESPONSE :: STATUS_CODE: {}, METHOD: {}, URL: {},";
    private static final String RESPONSE_LOG_FORMAT = RESPONSE_LOG_NO_BODY_FORMAT + ", BODY: {}";

    private static final String START_OF_PARAMS = "?";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String AUTHORIZATION = "Authorization";



    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(cachingRequest, cachingResponse);

        logRequestAndResponse(cachingRequest, cachingResponse);
        cachingResponse.copyBodyToResponse();
    }

    private void logRequestAndResponse(final ContentCachingRequestWrapper request,
                                       final ContentCachingResponseWrapper response) {
        logRequest(request);
        logResponse(request, response);
    }

    private void logRequest(final ContentCachingRequestWrapper request) {
        final String requestBody = new String(request.getContentAsByteArray());
        final String requestURIWithParams = getRequestURIWithParams(request);

        if (requestBody.isBlank()) {
            log.info(REQUEST_LOG_NO_BODY_FORMAT, request.getMethod(), requestURIWithParams);
            return;
        }
        log.info(REQUEST_LOG_FORMAT, request.getMethod(), requestURIWithParams, requestBody);
    }

    private void logResponse(final ContentCachingRequestWrapper request, final ContentCachingResponseWrapper response) {
        final Optional<String> jsonResponseBody = getJsonResponseBody(response);
        final String requestURIWithParams = getRequestURIWithParams(request);

        if (jsonResponseBody.isEmpty()) {
            log.info(RESPONSE_LOG_NO_BODY_FORMAT, response.getStatus(), request.getMethod(), requestURIWithParams);
            return;
        }
        log.info(RESPONSE_LOG_FORMAT, response.getStatus(), request.getMethod(), requestURIWithParams, jsonResponseBody.get());
    }

    private String getRequestURIWithParams(final ContentCachingRequestWrapper request) {
        final String requestURI = request.getRequestURI();
        final Map<String, String[]> params = request.getParameterMap();
        if (params.isEmpty()) {
            return requestURI;
        }
        final String parsedParams = parseParams(params);
        return requestURI + parsedParams;
    }

    private String parseParams(final Map<String, String[]> params) {
        final String everyParamStrings = params.entrySet().stream()
                .map(this::toParamString)
                .collect(Collectors.joining(PARAM_DELIMITER));
        return START_OF_PARAMS + everyParamStrings;
    }

    private String toParamString(final Map.Entry<String, String[]> entry) {
        final String key = entry.getKey();
        final StringBuilder builder = new StringBuilder();
        return Arrays.stream(entry.getValue())
                .map(value -> builder.append(key).append(KEY_VALUE_DELIMITER).append(value))
                .collect(Collectors.joining(PARAM_DELIMITER));
    }

    private Optional<String> getJsonResponseBody(final ContentCachingResponseWrapper response) {
        if (Objects.equals(response.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            return Optional.of(new String(response.getContentAsByteArray()));
        }
        return Optional.empty();
    }
}