package com.intuit.orderManagementSystem.order.management.system.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditJpaConfig {
    // No additional configuration needed
}