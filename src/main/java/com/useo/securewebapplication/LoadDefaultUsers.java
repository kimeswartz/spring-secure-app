package com.useo.securewebapplication;

import com.useo.securewebapplication.model.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class LoadDefaultUsers {

    private final MyUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoadDefaultUsers(MyUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        // Check if the admin user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            // Create the admin user with the predefined credentials
            MyUser adminUser = new MyUser();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("123")); // Hash the password
            adminUser.setRole("ADMIN");

            // Save the admin user to the repository
            userRepository.save(adminUser);
        }
    }
}

