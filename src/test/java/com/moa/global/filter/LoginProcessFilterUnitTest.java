package com.moa.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.domain.user.User;
import com.moa.global.exception.auth.BusinessAuthenticationException;
import com.moa.global.filter.LoginProcessFilter.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LoginProcessFilterUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationManager manager;

    @InjectMocks
    private LoginProcessFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @BeforeEach
    void init() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("로그인에 성공한다")
    void successLogin() throws IOException {
        //given
        request.setContentType("application/json");
        request.setMethod("POST");
        Login login = new Login("user@emailc.com", "password");
        User user = User.builder().email(login.email()).build();

        given(objectMapper.readValue(request.getInputStream(), Login.class)).willReturn(login);
        List<GrantedAuthority> authority = List.of(() -> "ROLE_USER");
        given(manager.authenticate(new UsernamePasswordAuthenticationToken(login.email(), login.password())))
                .willReturn(new UsernamePasswordAuthenticationToken(user, login.password(), authority));

        //when
        Authentication returnedAuthentication = filter.attemptAuthentication(request, response);

        //then
        assertThat(returnedAuthentication.getPrincipal()).isEqualTo(user);
        assertThat(returnedAuthentication.getAuthorities()).isEqualTo(authority);
    }

    @Test
    @DisplayName("request 오류로 인해 로그인에 실패한다")
    void failLoginByRequest() {
        //given
        request.setContentType("application/json");
        request.setMethod("GET");

        //when & then
        assertThatThrownBy(() -> filter.attemptAuthentication(request, response))
                .isInstanceOf(BusinessAuthenticationException.class);
    }

    @Test
    @DisplayName("로그인 정보 validation 실패로 로그인에 실패한다")
    void failLoginByLoginInfo() throws IOException {
        //given
        request.setContentType("application/json");
        request.setMethod("POST");

        Login login = new Login("user@emailc.com", "");

        given(objectMapper.readValue(request.getInputStream(), Login.class)).willReturn(login);


        //when & then
        assertThatThrownBy(() -> filter.attemptAuthentication(request, response))
                .isInstanceOf(BusinessAuthenticationException.class);
    }
}
