package com.sliit.safelocker.service;

import com.sliit.safelocker.exception.ResourceNotFoundException;
import com.sliit.safelocker.model.Role;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.repository.RoleRepository;
import com.sliit.safelocker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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


    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${email.username}")
    private String sendFromEmail;


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
        sendEmail(user);
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


    public void sendEmail(User user) {

        String name = user.getName();
        String email = user.getEmail();
        int otp = user.getOtp();


        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setFrom(sendFromEmail);

        msg.setSubject("Two Factor Authentication");
        msg.setText( "Dear " + name + System.lineSeparator()+  System.lineSeparator()+   "We are pleased to inform you that your research paper, “ ” has approved for present at the ICAF 2021."
                + " Please make sure to proceed the otp for the presentation."
                + " "+ otp + ""
                + System.lineSeparator()+  System.lineSeparator()+"Thank you !");
        javaMailSender.send(msg);

    }

}
