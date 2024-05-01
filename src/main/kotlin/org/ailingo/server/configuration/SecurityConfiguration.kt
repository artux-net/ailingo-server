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
            .headers { headers ->
                headers.frameOptions { frameOptions ->
                    frameOptions.disable()
                }
            }
//            .authorizeHttpRequests {
//                it.requestMatchers(*WHITE_LIST).permitAll()
//                    .anyRequest().authenticated()
//            }
           // .oauth2ResourceServer { obj: OAuth2ResourceServerConfigurer<HttpSecurity> -> obj.jwt(Customizer.withDefaults()) }
            .httpBasic(Customizer.withDefaults())
            .formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() }
            .build()
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

    /*@Value("\${jwt.secret.key}")
    var jwtKey: String = ""*/

    /*@Bean
    fun jwtDecoder(): JwtDecoder {
        val secretKey: SecretKey = SecretKeySpec(jwtKey.toByteArray(), "HMACSHA256")
        return NimbusJwtDecoder.withSecretKey(secretKey).build()
    }*/


   /* @Bean
    @Throws(
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        JOSEException::class
    )
    fun jwkSource(): JWKSource<SecurityContext> {
        val keySet = JWKSet(KeyGenerator.getInstance("HMACSHA256").provider.) // <- KeyGenerator is an external code
        return JWKSource { jwkSelector: JWKSelector, context: SecurityContext? ->
            jwkSelector.select(
                keySet
            )
        }
    }*/

    /*@Bean
    fun jwtEncoder(): JwtEncoder {
        val key: SecretKey = SecretKeySpec(jwtKey.toByteArray(), "HMACSHA256")
        val immutableSecret: JWKSource<SecurityContext> = ImmutableSecret(key)
        return NimbusJwtEncoder(immutableSecret)
    }*/

}