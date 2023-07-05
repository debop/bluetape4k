package io.bluetape4k.workshop.security.server.application.login

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.io.Serializable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:Email(regexp = ".+@.+\\..+")
    @field:NotBlank
    val username: String,

    @field:NotBlank
    @field:Size(min = 8, max = 255)
    val password: String,
): Serializable

fun LoginRequest.toUsernamePasswordAuthenticationToken(): UsernamePasswordAuthenticationToken =
    UsernamePasswordAuthenticationToken(username, password)
