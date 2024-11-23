package net.artux.ailingo.server.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "registration")
public class RegistrationConfig {
    private boolean confirmationEnabled;
    private Set<String> allowedEmails;
}
