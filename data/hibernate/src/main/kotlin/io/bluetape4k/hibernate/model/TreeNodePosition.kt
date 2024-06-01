package io.bluetape4k.hibernate.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class TreeNodePosition(
    @Column(name = "nodeLevel") val nodeLevel: Int = 0,
    @Column(name = "nodeOrder") val nodeOrder: Int = 0,
): Serializable
