package com.useo.securewebapplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecureWebApplication {


    private static final Logger log = LoggerFactory.getLogger(SecureWebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SecureWebApplication.class, args);
        log.info("Application started");
    }

}
