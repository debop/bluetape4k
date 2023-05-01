package io.bluetape4k.data.hibernate.mapping.tree

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.data.hibernate.model.LongJpaTreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity(name = "tree_treenode")
@Table(indexes = [Index(name = "ix_treenode_parent", columnList = "parent_id")])
@DynamicInsert
@DynamicUpdate
class TreeNode private constructor(
    @NotBlank
    var title: String,
): LongJpaTreeEntity<TreeNode>() {

    companion object {
        operator fun invoke(title: String): TreeNode {
            title.requireNotBlank("title")
            return TreeNode(title)
        }
    }

    var description: String? = null

    // NOTE: equals 를 재정의하지 않으면, Querydsl kapt 작업이 실패하는 경우가 있습니다.
    override fun equals(other: Any?): Boolean = super.equals(other)

    override fun equalProperties(other: Any): Boolean {
        return other is TreeNode && title == other.title
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: title.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("title", title)
            .add("description", description)
    }
}
