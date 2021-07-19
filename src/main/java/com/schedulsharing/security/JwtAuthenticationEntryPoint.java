package com.schedulsharing.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedulsharing.web.advice.ApiError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        if (authException instanceof UsernameNotFoundException) {
            getResponse(response, "UsernameNotFoundException", "해당 이메일의 계정이 없습니다.");
        }
        if (authException instanceof InsufficientAuthenticationException) {
            getResponse(response, "InsufficientAuthenticationException", "토큰이 잘못되었습니다.");
        }
        if (authException instanceof BadCredentialsException) {
            getResponse(response, "BadCredentialsException", "아이디와 비밀번호를 확인해주세요");
        }
    }

    private void getResponse(HttpServletResponse response, String error, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, error, message);

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}