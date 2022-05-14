package com.ludtek.micrometrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MicrometricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrometricsApplication.class, args);
    }

}
