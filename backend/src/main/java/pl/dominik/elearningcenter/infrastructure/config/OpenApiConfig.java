package pl.dominik.elearningcenter.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI elearningOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Learning Center API")
                        .description("REST API documentation for the E-Learning Center platform.")
                        .version("1.0.0")
                );
    }
}
