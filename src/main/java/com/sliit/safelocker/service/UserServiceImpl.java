package com.sliit.safelocker.service;

import com.sliit.safelocker.exception.ResourceNotFoundException;
import com.sliit.safelocker.model.Role;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.repository.RoleRepository;
import com.sliit.safelocker.repository.UserRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private Configuration config;


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

        String otpString = String.valueOf(user.getOtp());

        try {

            MimeMessage message = javaMailSender.createMimeMessage();


            Map<String, Object> model = new HashMap<>();

            model.put("otp", otpString);
            model.put("username", user.getName());

            // set mediaType
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());


            Template t = config.getTemplate("email_otp.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            helper.setTo(user.getEmail());
            helper.setFrom(sendFromEmail);
            helper.setText(html, true);
            helper.setSubject("TWO FACTOR AUTHENTICATOR");

            javaMailSender.send(message);

        }catch (MessagingException | IOException | TemplateException e) {

            System.out.println("error " + e);
        }

    }

}
