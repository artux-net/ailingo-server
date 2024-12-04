package net.artux.ailingo.server.configuration.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Long expiration;
    private RefreshToken refreshToken;

    @Getter
    @Setter
    public static class RefreshToken {
        private Long expiration;
    }
}