package io.bluetape4k.workshop.security.server.domain

import io.bluetape4k.core.ToStringBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import java.time.Instant

@Document
data class Customer(
    @Id val id: String,
    @Indexed(unique = true) val email: String,
    val password: String,
    val role: Role = Role.USER,
    @CreatedDate
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
): GrantedAuthority {

    override fun getAuthority(): String = "ROLE_$role"

    override fun equals(other: Any?): Boolean =
        other is Customer && EssentialCustomerData(this) == EssentialCustomerData(other)

    override fun hashCode(): Int = EssentialCustomerData(this).hashCode()

    override fun toString(): String {
        return ToStringBuilder(this)
            .add("id", id)
            .add("email", email)
            .add("role", role)
            .add("authority", authority)
            .toString()
    }
}

private data class EssentialCustomerData(val id: String) {
    constructor(customer: Customer): this(customer.id)
}

enum class Role {
    USER,
    ADMIN
}
