package com.useo.securewebapplication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

   @Autowired
   private PasswordEncoder passwordEncoder;

    public void saveUser(String username, String password, String email) {
        String cleanEmail = HtmlUtils.htmlEscape(email);
        String cleanPassword = HtmlUtils.htmlEscape(password);
        String hashedPassword = passwordEncoder.encode(cleanPassword);
        MyUsers.User newUser = new MyUsers.User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        newUser.setEmail(cleanEmail);
        userRepository.save(newUser);
    }
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    // Metod för att hämta alla användare
    public List<MyUsers.User> getAllUsers() {
        return userRepository.findAll();
    }
    public List<MyUsers.User> findUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public void logUserAccess(String email) {
        String maskedEmail = MaskingUtils.maskEmail(email);
        System.out.println("User access logged for: " + maskedEmail);
    }
}