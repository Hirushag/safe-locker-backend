package com.sliit.safelocker.controller;;
import com.sliit.safelocker.model.FileUpload;
import com.sliit.safelocker.model.Message;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.repository.MessageRepo;
import com.sliit.safelocker.response.CommonResponse;
import com.sliit.safelocker.service.UserService;
import com.sliit.safelocker.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


@Controller
@RequestMapping(path="api")
public class MessageController {
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String ALGORITHM = "AES";

    @Autowired
    private MessageRepo messageRepo;

    private final UserService userService;

    private KeyGenerator AESUtil;

    public MessageController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/v1/get-message/{username}")
    public ResponseEntity<?> getMessage(@RequestHeader(name="Authorization") String headerToken,
                                        @PathVariable("username") String username){

        String secretKey = "1234";

        List<Message> deCryptMessage = new ArrayList<>();
        try {
            String token = headerToken.substring("Bearer ".length());
            String userName = JwtTokenUtil.getUsernameByJwtToken(token);
            User user = userService.findByUsername(userName);
            if(user.getUsername().equals(username)){
                if(user.getRole().getCode().equals("ADMIN") || user.getRole().getCode().equals("EMPLOYEE") ){
                    List<Message> messages = messageRepo.getAllByUserId(user.getId());
                    for (Message message: messages) {
                        String msg =  decrypt(message.getMessage(),secretKey);
                        message.setMessage(msg);
                        deCryptMessage.add(message) ;
                    }

                    return ResponseEntity.ok(new CommonResponse<List<Message>>(true,null,deCryptMessage));
                }else {
                    return ResponseEntity.ok(new CommonResponse<String>(false,"You Have No Permission to send message",null ));
                }

            }else {
                return ResponseEntity.ok(new CommonResponse<String>(false,"Sorry! Your Request is Not Satisfied Our Security Policy",null));
            }

        }
        catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.ok(new CommonResponse<String>(false,"Server Not Responding....",null));		}




    }

    public String decrypt(String strToDecrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] message = cipher.doFinal(strToDecrypt.getBytes("UTF-8"));
            return Base64.getDecoder().decode(message).toString();
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;

    }
    public void prepareSecreteKey(String myKey) {
            MessageDigest sha = null;
            try {
                key = myKey.getBytes(StandardCharsets.UTF_8);
                sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16);
                secretKey = new SecretKeySpec(key, ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public String encrypt(String strToEncrypt, String secret) {
            try {
                prepareSecreteKey(secret);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
            } catch (Exception e) {
                System.out.println("Error while encrypting: " + e.toString());
            }
            return null;
        }





    @PostMapping(path="/v1/message/{username}")
    public  ResponseEntity<?>  addNewMessage (@RequestBody Message msg, @RequestHeader(name="Authorization") String headerToken,
                                              @PathVariable("username") String username) {

        String secretKey = "1234";
        String encryptedString = encrypt(msg.getMessage(), secretKey);
        try {
            String token = headerToken.substring("Bearer ".length());
            String userName = JwtTokenUtil.getUsernameByJwtToken(token);
            User user = userService.findByUsername(userName);
            if(user.getUsername().equals(username)){
                if(user.getRole().getCode().equals("ADMIN") || user.getRole().getCode().equals("EMPLOYEE") ){

                    Message m = new Message();
                    m.setMessage(encryptedString);
                    m.setUserId(user.getId());
                    messageRepo.save(m);

                    return ResponseEntity.ok(new CommonResponse<String>(true,null,"Successfully sent your message"));
                }else {
                    return ResponseEntity.ok(new CommonResponse<String>(false,"You Have No Permission to send message",null ));
                }

            }else {
                return ResponseEntity.ok(new CommonResponse<String>(false,"Sorry! Your Request is Not Satisfied Our Security Policy",null));
            }

        }
        catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.ok(new CommonResponse<String>(false,"Server Not Responding....",null));		}


    }


}
