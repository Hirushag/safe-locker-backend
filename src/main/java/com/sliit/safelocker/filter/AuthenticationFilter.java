package com.sliit.safelocker.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.safelocker.authentication.SafeLockerAuthenticationProvider;
import com.sliit.safelocker.model.SecurityUser;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.service.UserService;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private AuthenticationManager authenticationManagerBean;

    @Autowired
    private UserService userService;

    private SafeLockerAuthenticationProvider safeLockerAuthenticationProvider;


    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManagerBean = authenticationManager;
    }
    public AuthenticationFilter(SafeLockerAuthenticationProvider safeLockerAuthenticationProvider) {
        this.safeLockerAuthenticationProvider = safeLockerAuthenticationProvider;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Timestamp date = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

        int randomPIN = (int)(Math.random()*9000)+1000;
        User user = userService.findByUsername(request.getParameter("username"));
        user.setOtp(randomPIN);
        user.setOtpTime(date);
        user = userService.save(user);

        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("username in  InternalAuthenticationFilter is : {}", userName);
        log.info("password in  InternalAuthenticationFilter is {}  ", password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
        return safeLockerAuthenticationProvider.authenticate(authenticationToken);

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
