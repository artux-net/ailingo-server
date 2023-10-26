package org.ailingo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AilingoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AilingoServerApplication.class, args);
    }

}
