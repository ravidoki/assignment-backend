/** Swagger/OpenAPI configuration */
package com.gler.assignment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Assignment API", version = "1.0", description = "Text Replace and Forecast APIs"),
    servers = @Server(url = "/")
)
public class OpenApiConfig { }
