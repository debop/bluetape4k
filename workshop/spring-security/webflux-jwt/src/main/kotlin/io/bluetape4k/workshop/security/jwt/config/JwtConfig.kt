package io.bluetape4k.workshop.security.jwt.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import io.bluetape4k.logging.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.web.access.server.BearerTokenServerAccessDeniedHandler
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.config.EnableWebFlux
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
class JwtConfig {

    companion object: KLogging()

    @Value("\${jwt.public.key}")
    private lateinit var publicKey: RSAPublicKey

    @Value("\${jwt.private.key}")
    private lateinit var privateKey: RSAPrivateKey

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        authenticationManager: ReactiveAuthenticationManager,
        jwtEncoder: JwtEncoder,
        jwtDecoder: ReactiveJwtDecoder,
    ): SecurityWebFilterChain {
        return http.invoke {
            authorizeExchange {
                // 모든 resource 가 인증을 받아야 한다 (인증되지 않은 경우 401 예외가 발생한다)
                authorize(anyExchange, authenticated)
                authorize("/log-in", permitAll)
            }
            csrf { disable() }
            oauth2ResourceServer {
                jwt {
                    this.jwtDecoder = jwtDecoder
                }
            }
            httpBasic {
                this.authenticationManager = authenticationManager
            }
            exceptionHandling {
                authenticationEntryPoint = BearerTokenServerAuthenticationEntryPoint()
                accessDeniedHandler = BearerTokenServerAccessDeniedHandler()
            }
            formLogin {
                loginPage = "/log-in"
            }
        }
    }

    @Bean
    fun reactiveAuthenticationManager(passwordEncoder: PasswordEncoder): ReactiveAuthenticationManager {

        val userDetails = User.builder()
            .username("user")
            .password("password")
            .authorities("app")
            .passwordEncoder(passwordEncoder::encode)
            .build()

        val userDetailsService = MapReactiveUserDetailsService(userDetails)
        return UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk = RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build()
        val jwks = ImmutableJWKSet<SecurityContext>(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    @Bean
    fun jwtDecoder(): ReactiveJwtDecoder {
        return NimbusReactiveJwtDecoder
            .withPublicKey(this.publicKey)
            .build()
    }
}
