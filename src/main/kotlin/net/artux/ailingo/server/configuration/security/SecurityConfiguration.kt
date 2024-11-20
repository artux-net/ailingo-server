package net.artux.ailingo.server.configuration.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfiguration {

    @Autowired
    private lateinit var jwtFilter: JwtFilter

    @Autowired
    private lateinit var authenticationProvider: AuthenticationProvider

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(*WHITE_LIST).permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    companion object {
        private val WHITE_LIST = arrayOf(
            "/api/v1/user/login",
            "/api/v1/user/register",
            "/api/v1/user/reset/pass",
            "/api/v1/token",
            "/confirmation/register",
            "/mailing/**",
            "/reset",
            "/reset/**",
            "/v3/api-docs/*",
            "/swagger-ui/**",
            "/actuator/**",
            "/webjars/**"
        )
    }
}