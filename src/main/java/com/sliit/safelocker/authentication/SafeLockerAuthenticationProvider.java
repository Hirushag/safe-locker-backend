package com.sliit.safelocker.authentication;

import com.sliit.safelocker.model.SecurityUser;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.request.AuthRequest;
import com.sliit.safelocker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
@Slf4j
public class SafeLockerAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private  UserService userService;

    public SafeLockerAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        log.info("username in sltAutentication provider is "+username);
        log.info("password in sltAutentication provider is "+password);
        Boolean isOk;
        HttpEntity<AuthRequest> request = new HttpEntity<>(new AuthRequest(username,password));
        try{

//            ResponseEntity<JwtResponse> response =  restTemplate.exchange("http://tomcat.lakmobile.com:8080/cms/authenticate", HttpMethod.POST,request,JwtResponse.class);
//            ResponseEntity<SltAuthResponse> response =  restTemplate.exchange("https://sltmobitel.mocklab.io/api/User/AuthenticateUser", HttpMethod.POST,request, SltAuthResponse.class);

//             log.info(String.valueOf(response.getBody()));
//            log.info("response isSuccess "+ response.getBody().getIsSuccess());
            isOk= true; //response.getBody().getIsSuccess();

        }catch (Exception e){
            log.info("error occured "+e.getMessage());
            isOk = false;
        }

        if(isOk) {

            try{
                User user=   userService.findByUsername(username);
                log.info("internal user "+user);
                 if(user.getFlag().equals("Default")){
                     return  new UsernamePasswordAuthenticationToken(new SecurityUser(user),null,null);
                 }
                throw  new BadCredentialsException("Invalid credential");

            }catch (Exception e ){
                throw  new BadCredentialsException("Invalid credential");
            }

        }else{
            throw  new BadCredentialsException("Invalid credential");
        }


    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
