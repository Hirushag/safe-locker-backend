package com.sliit.safelocker.service;

import com.sliit.safelocker.exception.ResourceNotFoundException;
import com.sliit.safelocker.model.Role;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.repository.RoleRepository;
import com.sliit.safelocker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public User save(User user) {

        user.setFlag("Default");
        return userRepository.save(user);
    }

    @Override
    public User findById(long id) {


        if(userRepository.findById(id).isPresent()){
            return  userRepository.findById(id).get();
        }else {
            throw new ResourceNotFoundException("Couldn't find the internal user  with id " + id);
        }
    }

    @Override
    public List<User> findAll() {

        return userRepository.findAllByFlag("Default");
    }

    @Override
    public User updateById(User user) {

        Role  roleEntity = roleRepository.findById(user.getRole().getId()).get();
        if(userRepository.findById(user.getId()).isPresent()){
            User user1=  userRepository.findById(user.getId()).get();
            user1.setName(user.getName());
            user1.setActive(user.isActive());
            user1.setEmail(user.getEmail());
            return userRepository.save(user1);

        }else {
            throw new ResourceNotFoundException("Couldn't find the internal user  with id " + user.getId());
        }

    }

    @Override
    public void deleteById(long id) {
        if (userRepository.findById(id).isPresent() ) {
            User user = userRepository.findById(id).get();
            user.setFlag("Deleted");
            userRepository.save(user);
        }else {
            throw new ResourceNotFoundException("Couldn't find the internal user with id ");
        }


    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findInternalUserByUsername(username);
    }


    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    @Override
    public void otpRequest(User user) {
        Timestamp date = Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        int randomPIN = (int)(Math.random()*9000)+1000;
        user.setOtpTime(date);
        user.setOtp(randomPIN);
        userRepository.save(user);
    }

    @Override
    public User otpVerify(User user, int otp) {
        if(user.getOtp() == otp){
            user.setOtp(0);
            userRepository.save(user);
            return user;
        }else {
            return null;
        }

    }

}
