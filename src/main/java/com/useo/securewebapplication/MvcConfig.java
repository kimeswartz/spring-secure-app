package com.useo.securewebapplication;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/custom_logout").setViewName("custom_logout");

        registry.addViewController("/register").setViewName("/register");
        registry.addViewController("/delete").setViewName("delete_confirmation");
        registry.addViewController("/update_user").setViewName("update_user");

        registry.addViewController("/list").setViewName("list_users");

        registry.addViewController("/update_success").setViewName("update_success");
        registry.addViewController("/delete_success").setViewName("delete_success");
        registry.addViewController("/registration_success").setViewName("/registration_success");

    }
}