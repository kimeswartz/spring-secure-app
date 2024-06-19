package com.useo.securewebapplication;

import com.useo.securewebapplication.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    Optional<MyUser> findByUsername(String username);

    List<MyUser> findByRole(String role);

    Optional<MyUser> findByEmail(String email);

    Optional<MyUser> findByUsernameAndEmail(String username, String email);
}