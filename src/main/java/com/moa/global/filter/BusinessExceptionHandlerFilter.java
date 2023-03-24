package com.moa.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.global.exception.ErrorResponse;
import com.moa.global.exception.BusinessException;
import com.moa.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class BusinessExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException exception) {
            ErrorCode errorCode = exception.getErrorCode();
            response.setStatus(errorCode.getStatusCode());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(objectMapper.writeValueAsString(new ErrorResponse(errorCode.getCode(), messageSourceAccessor.getMessage(errorCode.getMessageCode()))));
        }
    }
}
