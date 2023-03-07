package com.sec.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
public class SecProjectBlockchainApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SecProjectBlockchainApplication.class, args);
    }

}
