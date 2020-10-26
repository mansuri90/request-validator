package com.theadex.requestvalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RequestStatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestStatsApplication.class, args);
    }

}
