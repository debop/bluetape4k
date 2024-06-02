package io.bluetape4k.workshop.r2dbc.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Table("users")
data class User(
    val name: String,
    val login: String,
    val email: String,
    val avatar: String? = null,
    @Id
    val id: Int? = null,
): Serializable

fun User.toDto(
    name: String = this.name,
    login: String = this.login,
    email: String = this.email,
    avatar: String? = this.avatar,
): UserDTO = UserDTO(name, login, email, avatar)
