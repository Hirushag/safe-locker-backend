package com.sliit.safelocker.service;

import com.sliit.safelocker.model.Role;
import com.sliit.safelocker.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User save(User internalUser);
    User findById(long id);
    List<User> findAll();
    User updateById(User internalUser);
    void deleteById(long id);
    User findByUsername(String username);
    List<User> findByRole(Role role);

    void otpRequest(User user);

    User otpVerify(User user, int otp);

}
