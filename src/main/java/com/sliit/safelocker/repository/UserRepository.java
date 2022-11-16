package com.sliit.safelocker.repository;
import com.sliit.safelocker.model.Role;
import com.sliit.safelocker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findInternalUserByUsername(String username);

    List<User> findAllByFlag(String flag);

    List<User> findAllByRole(Role role);

}
