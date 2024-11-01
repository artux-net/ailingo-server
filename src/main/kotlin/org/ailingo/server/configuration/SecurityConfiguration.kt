package org.ailingo.server.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .cors {
                it.configurationSource(corsConfigurationSource())
            }
            .headers { headers ->
                headers.frameOptions { frameOptions ->
                    frameOptions.disable()
                }
            }
            .authorizeHttpRequests {
                it.requestMatchers(*WHITE_LIST).permitAll()
                    .anyRequest().authenticated()
            }
           // .oauth2ResourceServer { obj: OAuth2ResourceServerConfigurer<HttpSecurity> -> obj.jwt(Customizer.withDefaults()) }
            .httpBasic(Customizer.withDefaults())
            .formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() }
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("ailingos.github.io", "*.artux.net, localhost") //  Или конкретные origins, например: listOf("https://localhost:8080")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешенные методы
        configuration.allowedHeaders = listOf("*") // Разрешенные заголовки
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Применяем конфигурацию ко всем endpoint'ам
        return source
    }

    companion object {
        private val WHITE_LIST = arrayOf(
            "/api/v1/user/register",
            "/confirmation/register",
            "/api/v1/user/reset/pass",
            "/api/v1/token",
            "/mailing/**",
            "/reset",
            "/reset/**",
            "/v3/api-docs/*",
            "/swagger-ui/**",
            "/actuator/**",
            "/webjars/**"
        )
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}