package com.talleresph.service_auth_usuarios;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Requiere PostgreSQL levantado y las variables de entorno SPRING_DATASOURCE_PASSWORD y JWT_SECRET.
// Para compilar/empaquetar sin base de datos: ./mvnw clean package -DskipTests
@SpringBootTest
class AuthApplicationTests {

    @Test
    void contextLoads() {
    }

}
