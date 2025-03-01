package org.schoolproject.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Autorise toutes les routes
                        .allowedOrigins("http://localhost:3000") // Votre frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Inclut OPTIONS pour les preflight
                        .allowedHeaders("Authorization", "Content-Type", "*") // Autorise tous les en-têtes, y compris Authorization
                        .exposedHeaders("Set-Cookie") // Permet au frontend de voir Set-Cookie si nécessaire
                        .allowCredentials(true); // Nécessaire pour les cookies avec withCredentials
            }
        };
    }
}