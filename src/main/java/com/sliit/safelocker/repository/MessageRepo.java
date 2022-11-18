package com.sliit.safelocker.repository;

import com.sliit.safelocker.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message,Long> {

    List<Message> getAllByUserId(Long id);
}