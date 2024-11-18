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

    @Bean
    public PerformanceProfiler performanceProfiler() {
        return new PerformanceProfiler();
    }

    public static class PerformanceProfiler {

        @Scheduled(fixedRate = 60000)
        public void profile() {
            // Add profiling logic here
            System.out.println("Profiling application performance...");
        }
    }
}
