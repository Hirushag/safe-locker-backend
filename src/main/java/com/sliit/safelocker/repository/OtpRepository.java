package com.sliit.safelocker.repository;
import com.sliit.safelocker.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp,Long> {


}
