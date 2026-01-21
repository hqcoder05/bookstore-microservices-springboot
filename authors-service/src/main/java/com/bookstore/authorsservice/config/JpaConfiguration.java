package com.bookstore.authorsservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.bookstore.authorsservice.repository")
@EnableJpaAuditing
public class JpaConfiguration {
}