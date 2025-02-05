package com.sec.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main project file, that provides the proper execution and application context.
 */
@SpringBootApplication
@EnableAsync
public class SecProjectClientApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SecProjectClientApplication.class, args);
    }

}
