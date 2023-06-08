package io.bluetape4k.workshop.r2dbc.domain

import java.io.Serializable

data class UserDTO(
    val name: String,
    val login: String,
    val email: String,
    val avatar: String? = null,
): Serializable

fun UserDTO.toModel(withId: Int? = null): User =
    User(
        name = this.name,
        login = this.login,
        email = this.email,
        avatar = this.avatar,
        id = withId
    )
