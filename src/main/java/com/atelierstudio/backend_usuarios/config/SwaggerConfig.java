package com.atelierstudio.backend_usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI () {
        return new OpenAPI()
                .info(new Info()
                        .title("API de usuarios Atelier Studio")
                        .version("1.0")
                        .description("Usuarios del administrador React: nombre, apellidos, correo, contraseña y rol."));
    }
}
