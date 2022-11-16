package com.sliit.safelocker.repository;

import com.sliit.safelocker.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    long findByCode(String code);

}
