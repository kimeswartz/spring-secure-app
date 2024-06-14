package com.useo.securewebapplication;

import com.useo.securewebapplication.model.MyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    // Logger for logging user authentication processes
    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    private final MyUserRepository userRepository;

    // Constructor injection for MyUserRepository
    public MyUserDetailsService(MyUserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by username: {}", username);

        // Fetch user by username from repository
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // Log warning if user is not found
                    logger.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        logger.debug("Username found: {}", username);

        // Return UserDetails instance for authentication
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        // Fetch user by email from repository
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    // Log warning if user is not found
                    logger.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        logger.debug("User email found: {}", email);

        // Return UserDetails instance for authentication
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}

