package com.useo.securewebapplication;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(@RequestParam(name = "search", required = false) String search, Model model) {
        List<MyUsers.User> users = (search == null) ? userService.getAllUsers() : userService.findUsersByUsername(search);
        if (users.isEmpty()) {
            model.addAttribute("message", "User could not be found");
        }
        users.forEach(user -> user.setEmail(MaskingUtils.maskEmail(user.getEmail())));
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid RegistrationDto registrationDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("registrationDto", registrationDto);
            return "register";
        }
        userService.saveUser(registrationDto.getUsername(), registrationDto.getPassword(), registrationDto.getEmail());
        return "redirect:/registrationsuccess";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long userId, RedirectAttributes redirectAttributes) {
        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/users";
    }

    @GetMapping("/logout-success")
    public String showLogoutPage() {
        return "logout";
    }
        @GetMapping("/403")
        public String forbidden() {
            return "403";
    }
}
