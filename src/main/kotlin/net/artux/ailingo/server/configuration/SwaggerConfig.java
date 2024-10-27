package net.artux.ailingo.server.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import net.artux.ailingo.server.service.ValuesService;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ValuesService valuesService;

    @Bean
    public GroupedOpenApi restApi() {
        return GroupedOpenApi.builder()
                .packagesToScan("org.ailingo.server.controller")
                .displayName("api")
                .group("rest")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .servers(List.of(new Server().url(valuesService.getAddress())))
                .info(new Info()
                        .title("ailingo")
                        .description("Сервисы REST Api. Для использования необходимо зарегистрироваться," +
                                " подтвердить почту и войти в аккаунт. Чтобы войти в аккаунт необходимо нажать" +
                                " на замок и ввести свои данные. <br>")
                        .version("0.1")
                        .license(null)
                )
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

}
