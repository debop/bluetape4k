package io.bluetape4k.workshop.security.server.application

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.security.server.application.security.JwtService
import io.bluetape4k.workshop.security.server.application.security.authentication.CustomerReactiveUserDetailsService
import io.bluetape4k.workshop.security.server.application.security.authorization.JwtReactiveAuthorizationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.codec.json.AbstractJackson2Decoder
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.router
import java.net.URI

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
class WebConfiguration: WebFluxConfigurer {

    companion object: KLogging() {
        val EXCLUDED_PATHS = arrayOf(
            "/login",
            "/",
            "/static/**",
            "/index.html",
            "/favicon.ico"
        )
    }

    @Suppress("DEPRECATION")
    @Bean
    fun configureSecurity(
        http: ServerHttpSecurity,
        jwtAuthenticationFilter: AuthenticationWebFilter,
        jwtService: JwtService,
    ): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .logout { it.disable() }
            .authorizeExchange {
                it.pathMatchers(*EXCLUDED_PATHS)
                    .permitAll()
                    .pathMatchers("/admin/**").hasRole("ADMIN")
                    .anyExchange().authenticated()
            }
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(JwtReactiveAuthorizationFilter(jwtService), SecurityWebFiltersOrder.AUTHORIZATION)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .build()
    }

    @Bean
    fun mainRouter() = router {
        accept(MediaType.TEXT_HTML).nest {
            GET("/") { temporaryRedirect(URI("/index.html")).build() }
        }
        resources("/**", ClassPathResource("public/"))
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenicationWebFilter(
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
        jwtConverter: ServerAuthenticationConverter,
        jwtServerAuthenticationSuccessHandler: ServerAuthenticationSuccessHandler,
        jwtServerAuthenticationFailureHandler: ServerAuthenticationFailureHandler,
    ): AuthenticationWebFilter {
        return AuthenticationWebFilter(reactiveAuthenticationManager)
            .apply {
                setRequiresAuthenticationMatcher { exchange ->
                    ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login").matches(exchange)
                }
                setServerAuthenticationConverter(jwtConverter)
                setAuthenticationSuccessHandler(jwtServerAuthenticationSuccessHandler)
                setAuthenticationFailureHandler(jwtServerAuthenticationFailureHandler)
                setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            }
    }

    // 이렇게 해도 되는데, Spring에서 제공하는 기본 기능을 사용하였다.
    //    @Bean
    //    fun jsonMapper(): JsonMapper = Jackson.defaultJsonMapper
    //
    //    @Bean
    //    fun jacksonDecoder(): AbstractJackson2Decoder =
    //        Jackson2JsonDecoder(jsonMapper(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_NDJSON)

    @Bean
    fun jacksonDecoder(): AbstractJackson2Decoder = Jackson2JsonDecoder()

    @Bean
    fun reactiveAuthenticationManager(
        reactiveUserDetailsService: CustomerReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): ReactiveAuthenticationManager {
        return UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService)
            .apply {
                setPasswordEncoder(passwordEncoder)
            }
    }
}
