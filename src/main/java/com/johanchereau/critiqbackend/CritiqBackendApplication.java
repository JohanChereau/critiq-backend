package com.johanchereau.critiqbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CritiqBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CritiqBackendApplication.class, args);
    }

}
