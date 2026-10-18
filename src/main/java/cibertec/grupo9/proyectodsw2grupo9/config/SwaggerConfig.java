package cibertec.grupo9.proyectodsw2grupo9.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Gestion de inventario y ventas")
                        .description("Gestion de inventario y ventas para accesorios informaticos")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Grupo 9")
                                .email("grupo9@cibertec.edu.pe"))
                        .license(new License().name("Cibertec License").url("https://ventastech.com.license")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Desarrollo local")
                        )

                );
    }
}
