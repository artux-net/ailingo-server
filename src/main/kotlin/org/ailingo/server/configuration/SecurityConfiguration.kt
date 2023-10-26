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


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests {
                it.requestMatchers(*WHITE_LIST).permitAll()
                    .anyRequest().authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() }
            .build()
    }

    companion object {
        private val WHITE_LIST = arrayOf(
            "/api/v1/user/register",
            "/confirmation/register",
            "/api/v1/user/reset/pass",
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