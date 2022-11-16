package com.sliit.safelocker.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.safelocker.authentication.SafeLockerAuthenticationProvider;
import com.sliit.safelocker.model.SecurityUser;
import com.sliit.safelocker.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private AuthenticationManager authenticationManagerBean;

    private SafeLockerAuthenticationProvider sltAuthenticationProvider;


    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManagerBean = authenticationManager;
    }
    public AuthenticationFilter(SafeLockerAuthenticationProvider sltAuthenticationProvider) {
        this.sltAuthenticationProvider = sltAuthenticationProvider;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("username in  InternalAuthenticationFilter is : {}", userName);
        log.info("password in  InternalAuthenticationFilter is {}  ", password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
        return sltAuthenticationProvider.authenticate(authenticationToken);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        SecurityUser user = (SecurityUser) authResult.getPrincipal();
        String username = user.getUsername();
        log.info("username in  InternalAuthenticationFilter is "+username);
        String issuer = request.getRequestURI().toString();
        String accessToken = JwtTokenUtil.createJwtToken(username, user.getRole().getCode(), issuer, JwtTokenUtil.ACCESS);
        String refreshToken = JwtTokenUtil.createJwtToken(username, user.getRole().getCode(), issuer, JwtTokenUtil.REFRESH);
        response.setContentType(APPLICATION_JSON_VALUE);
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
       // super.unsuccessfulAuthentication(request, response, failed);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(401);
        Map<String, Object> failResponse = new HashMap<>();
        failResponse.put("isSuccess", false);
        failResponse.put("errorMessage", "Invalid credential");
        new ObjectMapper().writeValue(response.getOutputStream(), failResponse);
    }
}
