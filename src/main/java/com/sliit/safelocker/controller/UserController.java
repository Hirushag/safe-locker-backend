package com.sliit.safelocker.controller;

import com.sliit.safelocker.mapper.UserDto;
import com.sliit.safelocker.mapper.UserDtoMapper;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.response.CommonResponse;
import com.sliit.safelocker.service.UserService;
import com.sliit.safelocker.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDtoMapper userDtoMapper;


    @PostMapping({"/v1/user"})
    public ResponseEntity<?> saveUser(@RequestBody UserDto dto) {

        User usr = userDtoMapper.mapFrom(dto);
        usr.setPassword(passwordEncoder.encode(dto.getPassword()));
        User userResponse = userService.save(usr);
        return ResponseEntity.ok(CommonResponse.<User>builder().isSuccess(true).dataBundle(userResponse).build());

    }


    @GetMapping({"/v1/users"})
    public ResponseEntity<?> getUsers() {
        List<User> internalUserList = userService.findAll();
        return ResponseEntity.ok(CommonResponse.<List<User>>builder().isSuccess(true).dataBundle(internalUserList).build());

    }

    @GetMapping({"/v1/users/{id}"})
    public ResponseEntity<?> getUserById(@PathVariable("id") long id) {
        User internalUser = userService.findById(id);

        return ResponseEntity.ok(CommonResponse.<User>builder().isSuccess(true).dataBundle(internalUser).build());

    }
    @GetMapping({"/v1/users/by-token"})
    public ResponseEntity<?> getInternalUserByAccessToken(@RequestHeader(name = "Authorization") String headerToken) {

        String token = headerToken.substring("Bearer ".length());
        String userName = JwtTokenUtil.getUsernameByJwtToken(token);
        User user = userService.findByUsername(userName);
        return ResponseEntity.ok(new CommonResponse<User>(true,"",user));

    }

    @PutMapping({"/v1/users"})
    public ResponseEntity<?> updateUser(@RequestBody UserDto dto) {
        User user = userDtoMapper.mapFrom(dto);
        User updatedInternalUser = userService.updateById(user);
        return ResponseEntity.ok(CommonResponse.<User>builder().isSuccess(true).dataBundle(updatedInternalUser).build());
    }

    @DeleteMapping({"/v1/users/{id}"})
    public ResponseEntity<?> delete(@PathVariable("id") long id){
        userService.deleteById(id);
        return ResponseEntity.ok(CommonResponse.<Integer>builder().isSuccess(true).dataBundle(1).build());
    }


}
