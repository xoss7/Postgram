package sn.edu.ept.postgram.gatewayservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.GET, "/api/v1/media/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/profiles/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/follows/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/posts/*/comments").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }
}