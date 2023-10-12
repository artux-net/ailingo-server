package org.ailingo.server.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import net.artux.pdanetwork.service.util.ValuesService;
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
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ValuesService valuesService;

    private final String[] adminPaths = {"/api/v1/admin/**", "/api/v1/**/admin/**"};

    @Bean(name = "restApi")
    public GroupedOpenApi restApi() {
        return GroupedOpenApi.builder()
                .displayName("pda-api")
                .group("pdanetwork-rest")
                .pathsToMatch("/api/v1/**")
                .pathsToExclude(adminPaths)
                .build();
    }

    @Bean(name = "restAdminApi")
    public GroupedOpenApi restAdminApi() {
        return GroupedOpenApi.builder()
                .group("pdanetwork-rest-admin")
                .pathsToMatch(adminPaths)
                .displayName("admin-api")
                .build();
    }

    @Bean
    public OpenAPI openApi() {
        Server server = new Server();
        server.setUrl(valuesService.getAddress());

        Contact contact = new Contact();
        contact.setName("Maxim");
        contact.setUrl("https://t.me/prygunovx");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("PDANETWORK")
                        .description("Сервисы REST Api. Для использования необходимо зарегистрироваться," +
                                " подтвердить почту и войти в аккаунт. Чтобы войти в аккаунт необходимо нажать" +
                                " на замок и ввести свои данные. <br>"
                                + "<br> <a href=\"" + valuesService.getPostmanTeamLink() + "\">Postman Workspace</a>"
                                + "<br> <a href=\"" + valuesService.getPostmanInviteLink() + "\">Приглашение в Postman</a> <br>"
                                + "<br> <a href=\"" + valuesService.getAdminAddress() + "\">Панель администратора</a> <br>"
                                + "<br> <a href=\"" + valuesService.getAddress() + "/enc\">Энциклопедия</a>")
                        .contact(contact)
                        .version("0.4")
                        .license(null)
                )
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }

}
