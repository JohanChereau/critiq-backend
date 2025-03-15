package com.johanchereau.critiqbackend;

import com.johanchereau.critiqbackend.user.UserRole;
import com.johanchereau.critiqbackend.user.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class CritiqBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CritiqBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRoleRepository userRoleRepository) {
        return args -> {
            if(userRoleRepository.findByName("USER").isEmpty()) {
                userRoleRepository.save(UserRole.builder().name("USER").build());
            }
        };
    }

}
