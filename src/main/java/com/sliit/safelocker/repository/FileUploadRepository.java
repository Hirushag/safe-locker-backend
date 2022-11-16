package com.sliit.safelocker.repository;

import com.sliit.safelocker.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileUpload,Long> {

    List<FileUpload> findAllByUsername(String username);
}
