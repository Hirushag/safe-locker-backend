package com.sliit.safelocker.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class JwtRequestFilter  extends OncePerRequestFilter {




//    @Autowired
//    private SystemLogService systemLogService;

    private final int responseMaxLength = 1000;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


            String requestUrl = request.getRequestURI();
            ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);
            log.info("once per filter working 2 ");
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    int t = "Bearer ".length();
                    log.info("substring length {}", t);
                    String token = authorizationHeader.substring("Bearer ".length());
                    log.info(token);
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String role = String.valueOf(decodedJWT.getClaim("role"));
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//                stream(roles).forEach(role -> {
//                    authorities.add(new SimpleGrantedAuthority(role));
//                });
                    authorities.add(new SimpleGrantedAuthority("ADMIN"));
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    LocalDateTime accessDateTime = LocalDateTime.now();
                    long currentTime = System.currentTimeMillis();
                    filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
                    long timeTake = System.currentTimeMillis() - currentTime;
                    //addSystemLog(request,response,username,accessDateTime, timeTake, contentCachingResponseWrapper,contentCachingRequestWrapper);

                } catch (Exception e) {

                    log.error("Error login in {} ", e.getMessage());
                    response.setStatus(401);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    Map<String, Object> error = new HashMap<>();
                    error.put("error_message", e.getMessage());
                    error.put("isSuccess", false);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }

            } else {
                log.info("once per filter working 3 ");
                LocalDateTime accessDateTime = LocalDateTime.now();
                long currentTime = System.currentTimeMillis();
                filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
                long timeTake = System.currentTimeMillis() - currentTime;
                //addSystemLog(request,response,null,accessDateTime, timeTake, contentCachingResponseWrapper,contentCachingRequestWrapper);
            }

        contentCachingResponseWrapper.copyBodyToResponse();
        }

//    private void addSystemLog(HttpServletRequest request,
//                              HttpServletResponse response,
//                              String username, LocalDateTime accessDateTime,
//                              long timeTake,
//                              ContentCachingResponseWrapper contentCachingResponseWrapper,
//                              ContentCachingRequestWrapper contentCachingRequestWrapper) {
//
//        String stringRequest = getStringValue(contentCachingRequestWrapper.getContentAsByteArray(), request.getCharacterEncoding());
//        String stringResponse = getStringValue(contentCachingResponseWrapper.getContentAsByteArray(), response.getCharacterEncoding());
//
//        SystemLog systemLog = new SystemLog();
//
//        if (username != null) {
//            systemLog.setUsername(username);
//        } else {
//            if (request.getParameter("username") != null){
//                systemLog.setUsername(request.getParameter("username"));
//            }else {
//                systemLog.setUsername(null);}
//        }
//        systemLog.setStatus(response.getStatus());
//        systemLog.setRequestMethod(request.getMethod());
//        systemLog.setAccessOn(accessDateTime);
//        systemLog.setChannel("C-Panel");
//        systemLog.setEndPoint(request.getRequestURI());
//        systemLog.setResponseTime(timeTake + " ms");
//        Map<String, String> result = JwtTokenUtil.truncateRequestAndResponse(stringRequest,stringResponse);
//        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
//            systemLog.setRequestBody(result.get("limitRequest"));
//            systemLog.setResponse(result.get("limitResponse"));
//            try {
//                systemLogService.save(systemLog);
//            }catch (Exception e){
//                log.info("error in  save system log " + e);}
//        } else {
//               systemLog.setRequestBody(null);
//            if (responseMaxLength < stringResponse.length()) {
//                systemLog.setResponse(result.get("limitResponse"));
//            } else {
//                systemLog.setResponse(stringResponse);
//            }
//            try {
//                systemLogService.save(systemLog);
//            }catch (Exception e){
//                log.info("error in  save system log " + e);}
//        }
//
//    }
//

    private String getStringValue ( byte[] contentAsByteArray, String characterEncoding){
        try {
            return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
