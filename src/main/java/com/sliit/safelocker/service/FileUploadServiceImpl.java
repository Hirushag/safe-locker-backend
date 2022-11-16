package com.sliit.safelocker.service;

import com.sliit.safelocker.exception.FileStorageException;
import com.sliit.safelocker.model.FileUpload;
import com.sliit.safelocker.repository.FileUploadRepository;
import com.sliit.safelocker.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadRepository fileUploadRepository;

    public FileUploadServiceImpl(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }


    @Override
    public FileUpload saveFile(MultipartFile file, String username) throws NoSuchAlgorithmException, IOException {

        FileUpload fileUpload = new FileUpload();
        byte[] baseFile = Base64.encodeBase64(file.getBytes());
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
        String hashCode = FileUtil.getFileChecksum(shaDigest,file.getInputStream());
        FileUpload fileUpload1 = FileUtil.fileUpload(file);
        fileUpload.setFileName(fileUpload1.getFileName());
        fileUpload.setHash(hashCode);
        fileUpload.setFileUrl(fileUpload1.getFileUrl());
        fileUpload.setUsername(username);
        fileUpload = fileUploadRepository.save(fileUpload);
        return fileUpload;
    }

    @Override
    public List<FileUpload> findFileList(String username) {
        return fileUploadRepository.findAllByUsername(username);
    }

    @Override
    public String findFileById(Long id) throws NoSuchAlgorithmException, IOException {

       FileUpload fileUpload = fileUploadRepository.findById(id).get();
        String path = "C:"+File.separator+"xampp"+File.separator+"htdocs"+File.separator+"safe_locker"+File.separator+"file"+File.separator+fileUpload.getFileName();
        File file = new File(path);
        if (!file.exists()) {
            throw new FileStorageException("File not found");
        } else {
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            InputStream targetStream = new FileInputStream(file);
            String hashCode = FileUtil.getFileChecksum(shaDigest,targetStream);
            if (hashCode.equals(fileUpload.getHash())){
                return fileUpload.getFileUrl();
            }else {
                return null;
            }
        }
    }


}
