package net.artux.ailingo.server.configuration.security

import net.artux.ailingo.server.jwt.conf.JwtFilter
import net.artux.ailingo.server.service.impl.UserDetailServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfiguration(
    private val jwtFilter: JwtFilter,
    private val userDetailService: UserDetailServiceImpl
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .cors(Customizer.withDefaults())
            .headers { headers ->
                headers.frameOptions { frameOptions ->
                    frameOptions.disable()
                }
            }
            .authorizeHttpRequests {
                it.requestMatchers(*WHITE_LIST).permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(
                DaoAuthenticationProvider().apply {
                    setUserDetailsService(userDetailService)
                    setPasswordEncoder(BCryptPasswordEncoder())
                }
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    companion object {
        private val WHITE_LIST = arrayOf(
            "/api/v1/user/login",
            "/api/v1/user/register",
            "/api/v1/user/verify-email",
            "/api/v1/user/resend-verification-code",
            "/api/v1/user/refresh-token",
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
