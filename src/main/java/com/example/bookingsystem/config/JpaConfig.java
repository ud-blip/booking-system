package com.example.bookingsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.bookingsystem.infrastructure.repository")
public class JpaConfig {
}
