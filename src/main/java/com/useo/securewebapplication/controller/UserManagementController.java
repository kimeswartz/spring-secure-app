package com.useo.securewebapplication.controller;

import com.useo.securewebapplication.MyUserDto;
import com.useo.securewebapplication.MyUserRepository;
import com.useo.securewebapplication.UpdateUserDto;
import com.useo.securewebapplication.model.MyUser;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import com.useo.securewebapplication.MaskingUtils;

import java.util.List;
import java.util.Optional;

@Controller
public class UserManagementController {

    // Logger for logging controller actions
    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    private final MyUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManagementController(MyUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String showHomePage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }
        return "home";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }

    @GetMapping("/logout-success")
    public String showLogoutPage() {
        return "custom_logout";
    }

    @GetMapping("/list")
    public String listUsers(@RequestParam(value = "search", required = false) String searchUsername, Model model) {
        // Logging fetching list of users
        logger.debug("Fetching list of users");

        if (searchUsername != null && !searchUsername.isEmpty()) {
            // Logging searching for user by username
            logger.debug("Searching for user by username: {}", searchUsername);
            Optional<MyUser> searchedUserOptional = userRepository.findByUsername(searchUsername);
            if (searchedUserOptional.isPresent()) {
                MyUser searchedUser = searchedUserOptional.get();
                // Logging user found by username
                logger.debug("Found user by username: {}", searchUsername);
                model.addAttribute("searchedUser", searchedUser);
            } else {
                // Logging user not found by username
                logger.debug("User not found by username: {}", searchUsername);
            }
        }

        // Fetching all users
        List<MyUser> users = userRepository.findByRole("USER");
        // Logging number of users found with role USER
        logger.debug("Found {} users with role USER", users.size());
        model.addAttribute("users", users);
        return "list_users";
    }

    // GET handler to show the update form
    @GetMapping("/update_user")
    public String showUpdateUserForm(@RequestParam("username") String username, Model model) {
        logger.debug("Fetching user details for update by username: {}", username);

        Optional<MyUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            UpdateUserDto updateUserDto = new UpdateUserDto();
            updateUserDto.setNewUsername(user.get().getUsername());
            updateUserDto.setEmail(user.get().getEmail());

            model.addAttribute("updateUserDto", updateUserDto);
            model.addAttribute("oldUsername", username);
            return "update_user";
        } else {
            logger.debug("User not found by username: {}", username);
            model.addAttribute("error", "User not found.");
            return "error_page";
        }
    }

    @PostMapping("/update_user")
    public String updateUser(@RequestParam("oldUsername") String oldUsername,
                             @Valid @ModelAttribute("updateUserDto") UpdateUserDto updateUserDto,
                             BindingResult bindingResult,
                             Model model) {
        logger.debug("Updating user details for user with old username: {}", oldUsername);

        if (bindingResult.hasErrors()) {
            logger.debug("Validation errors occurred during user update");
            model.addAttribute("oldUsername", oldUsername);
            return "update_user";
        }

        Optional<MyUser> userOptional = userRepository.findByUsername(oldUsername);
        if (userOptional.isPresent()) {
            MyUser existingUser = userOptional.get();

            Optional<MyUser> userWithNewUsername = userRepository.findByUsername(updateUserDto.getNewUsername());
            Optional<MyUser> userWithNewEmail = userRepository.findByEmail(updateUserDto.getEmail());

            if ((userWithNewUsername.isPresent() && !userWithNewUsername.get().getId().equals(existingUser.getId())) ||
                    (userWithNewEmail.isPresent() && !userWithNewEmail.get().getId().equals(existingUser.getId()))) {
                logger.debug("Username or email already exists for another user");
                model.addAttribute("error", "Username and email must be unique.");
                model.addAttribute("oldUsername", oldUsername);
                return "update_user";
            }

            existingUser.setUsername(updateUserDto.getNewUsername());
            existingUser.setPassword(passwordEncoder.encode(updateUserDto.getNewPassword()));

            String anonymizedEmail = MaskingUtils.anonymizeEmail(updateUserDto.getEmail());
            existingUser.setEmail(HtmlUtils.htmlEscape(anonymizedEmail));

            try {
                userRepository.save(existingUser);
                logger.debug("User details updated successfully for username: {}", updateUserDto.getNewUsername());
                return "update_success";
            } catch (Exception e) {
                logger.debug("Unexpected error occurred while updating user details", e);
                model.addAttribute("error", "Unexpected error occurred. Please try again.");
                model.addAttribute("oldUsername", oldUsername);
                return "update_user";
            }
        } else {
            logger.debug("User not found by username: {}", oldUsername);
            model.addAttribute("error", "User not found.");
            return "error_page";
        }
    }

    @GetMapping("/delete")
    public String showDeleteConfirmationForm(@RequestParam("username") String username, Model model) {
        // Logging fetching delete confirmation form for username
        logger.debug("Fetching delete confirmation form for username: {}", username);

        model.addAttribute("username", username);
        return "delete_confirmation";

    }

    @PostMapping("/delete")
    public String removeUser(@RequestParam("username") String username,
                             @RequestParam("confirmUsername") String confirmUsername,
                             Model model) {
        // Logging removing user with username
        logger.debug("Removing user with username: {}", username);

        username = HtmlUtils.htmlEscape(username);
        confirmUsername = HtmlUtils.htmlEscape(confirmUsername);

        if (username.equals(confirmUsername)) {
            Optional<MyUser> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                userRepository.delete(user.get());
                // Logging user removed successfully with username
                logger.debug("User with username {} removed successfully", username);
                return "delete_success";
            } else {
                // Logging user not found by username
                logger.debug("User not found by username: {}", username);
                model.addAttribute("error", "User not found.");
                return "error_page";
            }
        } else {
            // Logging confirmation username doesn't match for user deletion
            logger.debug("Confirmation username doesn't match for user deletion");
            model.addAttribute("error", "Confirmation username doesn't match.");
            return "error_page";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Logging fetching registration form
        logger.debug("Fetching registration form");

        model.addAttribute("user", new MyUserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") MyUserDto userDto,
                           BindingResult bindingResult,
                           Model model) {
        // Logging processing registration for user
        logger.debug("Processing registration for user: {}", userDto.getUsername());

        if (bindingResult.hasErrors()) {
            // Logging validation errors occurred during registration
            logger.debug("Validation errors occurred during registration");
            return "register"; // return to registration form to display errors
        }

        try {
            // Check if username already exists
            MyUser existingUsername = userRepository.findByUsername(userDto.getUsername()).orElse(null);
            if (existingUsername != null) {
                // Logging username already exists
                logger.debug("Username already exists: {}", userDto.getUsername());
                model.addAttribute("myUserDto", userDto);
                return "registration_error";
            }

            // Check if email already exists
            MyUser existingEmail = userRepository.findByEmail(userDto.getEmail()).orElse(null);
            if (existingEmail != null) {
                // Logging email already exists
                logger.debug("Email already exists: {}", userDto.getEmail());
                model.addAttribute("myUserDto", userDto);
                return "registration_error";
            }

            // Create and save new user
            MyUser newUser = new MyUser();
            newUser.setUsername(HtmlUtils.htmlEscape(userDto.getUsername())); // Escape HTML to prevent XSS
            newUser.setPassword(passwordEncoder.encode(HtmlUtils.htmlEscape(userDto.getPassword()))); // Encode password

            // Optional: Anonymize email if needed (assuming MaskingUtils is a utility class you have)
            String anonymizedEmail = MaskingUtils.anonymizeEmail(userDto.getEmail());
            newUser.setEmail(HtmlUtils.htmlEscape(anonymizedEmail)); // Escape email as well

            newUser.setRole("USER");

            userRepository.save(newUser); // Save new user to repository

            // Logging user registered successfully
            logger.debug("User registered successfully: {}", userDto.getUsername());
            return "registration_success";
        } catch (Exception e) {
            // Logging unexpected error occurred during registration
            logger.debug("Unexpected error occurred during registration", e); // Include exception in log
            model.addAttribute("error", "Unexpected error occurred. Please try again.");
            return "registration_error";
        }
    }
}
