package com.sliit.safelocker.repository;
import com.sliit.safelocker.model.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SystemLogRepository extends JpaRepository<SystemLog,Long> {
}
