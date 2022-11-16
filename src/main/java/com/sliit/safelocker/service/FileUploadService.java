package com.sliit.safelocker.service;

import com.sliit.safelocker.model.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface FileUploadService {

    FileUpload saveFile(MultipartFile file, String username) throws NoSuchAlgorithmException, IOException;

    List<FileUpload> findFileList(String username);

    String findFileById(Long id) throws NoSuchAlgorithmException, IOException;
}
