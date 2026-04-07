package sn.edu.ept.postgram.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import sn.edu.ept.postgram.authservice.entity.User;
import sn.edu.ept.postgram.authservice.repository.UserRepository;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.admin.email}")
    private String adminEmail;

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner createAdminUserIfNotExists(UserRepository userRepository) {
        if (!userRepository.existsByUsername(adminUsername)) {
            return args -> {
                User admin = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder().encode(adminPassword))
                        .email(adminEmail)
                        .roles(List.of("ROLE_ADMIN", "ROLE_USER"))
                        .build();
                userRepository.save(admin);
            };
        }
        return args -> {};
    }
}