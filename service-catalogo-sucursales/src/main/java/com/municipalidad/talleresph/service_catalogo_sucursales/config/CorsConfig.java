package com.municipalidad.talleresph.service_catalogo_sucursales.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos para navegador (Ionic) y dispositivos móviles (Capacitor)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:8100",
                "capacitor://localhost",
                "http://localhost"
        ));
        
        // Métodos HTTP requeridos incluyendo PATCH
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Encabezados requeridos para JWT y JSON
        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "Accept", "Origin"
        ));
        
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return new CorsFilter(source);
    }
}
