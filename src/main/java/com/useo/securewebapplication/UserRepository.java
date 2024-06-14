package com.useo.securewebapplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<MyUsers.User, Long> {
    List<MyUsers.User> findByUsernameContainingIgnoreCase(String username);
}