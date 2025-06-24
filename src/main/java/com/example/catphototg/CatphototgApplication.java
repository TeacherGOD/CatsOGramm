package com.example.catphototg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CatphototgApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatphototgApplication.class, args);
    }

}
