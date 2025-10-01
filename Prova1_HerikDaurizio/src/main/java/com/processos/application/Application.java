package com.processos.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.processos")
@EntityScan(basePackages = "com.processos.model")  // Escaneia entidades
@EnableJpaRepositories(basePackages = "com.processos.repository")  // Escaneia repositórios
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}







