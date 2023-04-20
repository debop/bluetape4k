package io.bluetape4k.data.hibernate.model

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class TreeNodePosition(
    @Column(name = "nodeLevel") val nodeLevel: Int = 0,
    @Column(name = "nodeOrder") val nodeOrder: Int = 0,
): Serializable
