package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAspectJAutoProxy
@SpringBootApplication
public class TreeWithNodesApplication {

    public static void main(String[] args) {
        SpringApplication.run(TreeWithNodesApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("Application started");
    }
}
