package com.smartjobportal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.smartjobportal.entity.Role;
import com.smartjobportal.entity.User;
import com.smartjobportal.repository.UserRepository;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.full-name:System Administrator}")
    private String adminFullName;

    public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.countByRole(Role.ADMIN) > 0) {
            return;
        }

        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            log.info("No admin exists yet. Set app.bootstrap.admin.email and app.bootstrap.admin.password to bootstrap one automatically.");
            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Cannot bootstrap admin: email {} already exists with a non-admin account.", normalizedEmail);
            return;
        }

        if (adminPassword.length() < 6) {
            log.warn("Cannot bootstrap admin: app.bootstrap.admin.password must be at least 6 characters.");
            return;
        }

        User admin = new User();
        admin.setEmail(normalizedEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setFullName(StringUtils.hasText(adminFullName) ? adminFullName.trim() : "System Administrator");
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
        log.info("Bootstrapped initial admin account: {}", normalizedEmail);
    }
}
