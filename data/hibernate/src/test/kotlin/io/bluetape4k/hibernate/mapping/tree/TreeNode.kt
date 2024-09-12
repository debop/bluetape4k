package io.bluetape4k.hibernate.mapping.tree

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.hibernate.model.AbstractJpaTreeEntity
import io.bluetape4k.support.requireNotBlank
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

@Entity(name = "tree_treenode")
@Table(indexes = [Index(name = "ix_treenode_parent", columnList = "parent_id")])
@DynamicInsert
@DynamicUpdate
class TreeNode private constructor(
    @NotBlank var title: String,
): AbstractJpaTreeEntity<TreeNode, Long>() {

    companion object {
        @JvmStatic
        operator fun invoke(title: String): TreeNode {
            title.requireNotBlank("title")
            return TreeNode(title)
        }
    }

    @field:Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null

    var description: String? = null

    // NOTE: equals 를 재정의하지 않으면, Querydsl kapt 작업이 실패하는 경우가 있습니다.
    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun equalProperties(other: Any): Boolean {
        return other is TreeNode && title == other.title
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: title.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper().add("title", title).add("description", description)
    }
}
