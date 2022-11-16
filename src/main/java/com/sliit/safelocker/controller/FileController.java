package com.sliit.safelocker.controller;

import com.sliit.safelocker.model.FileUpload;
import com.sliit.safelocker.model.User;
import com.sliit.safelocker.response.CommonResponse;
import com.sliit.safelocker.service.FileUploadService;
import com.sliit.safelocker.service.UserService;
import com.sliit.safelocker.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api")
public class FileController {

    private final UserService userService;

    private final FileUploadService fileUploadService;

    public FileController(UserService userService, FileUploadService fileUploadService) {
        this.userService = userService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/v1/file-upload/user/{username}")
    public ResponseEntity<?> uploadResearchPaper(@RequestParam("file") MultipartFile file,
                                                 @RequestHeader(name="Authorization") String headerToken,
                                                 @PathVariable("username") String username) {
        try {
            String token = headerToken.substring("Bearer ".length());
            String userName = JwtTokenUtil.getUsernameByJwtToken(token);
            User user = userService.findByUsername(userName);
            if(user.getUsername().equals(username)){
                if(user.getRole().getCode().equals("ADMIN")){
                    FileUpload fileUpload = fileUploadService.saveFile(file,username);
                    return ResponseEntity.ok(new CommonResponse<FileUpload>(true,null,fileUpload));
                }else {
                    return ResponseEntity.ok(new CommonResponse<String>(false,"You Have No Permission to Upload file",null ));
                }

            }else {
                return ResponseEntity.ok(new CommonResponse<String>(false,"Sorry! Your Request is Not Satisfied Our Security Policy",null));
            }

        }
        catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.ok(new CommonResponse<String>(false,"Server Not Responding....",null));		}


    }


    @GetMapping({"/v1/file-upload/user/{username}"})
    public ResponseEntity<?> getFileList(@PathVariable("username") String username,
                                         @RequestHeader(name="Authorization") String headerToken) {
       try {

        String token = headerToken.substring("Bearer ".length());
        String userName = JwtTokenUtil.getUsernameByJwtToken(token);
        User user = userService.findByUsername(userName);
        if(user.getUsername().equals(username)){
            if(user.getRole().getCode().equals("ADMIN")){
                List<FileUpload> files = fileUploadService.findFileList(username);
                return ResponseEntity.ok(new CommonResponse<List<FileUpload>>(true,null,files));
            }else {
                return ResponseEntity.ok(new CommonResponse<String>(false,"You Have No Permission to View file",null ));
            }

        }else {
            return ResponseEntity.ok(new CommonResponse<String>(false,"Sorry! Your Request is Not Satisfied Our Security Policy",null));
        }

    }
        catch (Exception e) {
        System.out.println(e);
        return ResponseEntity.ok(new CommonResponse<String>(false,"Server Not Responding....",null));		}



    }


    @GetMapping({"/v1/file-upload/{id}/user/{username}"})
    public ResponseEntity<?> getFile(@PathVariable("username") String username,@PathVariable("id") Long id,
                                         @RequestHeader(name="Authorization") String headerToken) {
        try {

            String token = headerToken.substring("Bearer ".length());
            String userName = JwtTokenUtil.getUsernameByJwtToken(token);
            User user = userService.findByUsername(userName);
            if(user.getUsername().equals(username)){
                if(user.getRole().getCode().equals("ADMIN")){
                    String file = fileUploadService.findFileById(id);
                    if(file == null){
                        return ResponseEntity.ok(new CommonResponse<String>(false,"Your File is Crashed!!",null));
                    } else {
                        return ResponseEntity.ok(new CommonResponse<String>(true,null,file));

                    }

                }else {
                    return ResponseEntity.ok(new CommonResponse<String>(false,"You Have No Permission to View file",null ));
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
