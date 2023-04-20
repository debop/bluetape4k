package io.bluetape4k.data.hibernate.reactive.examples.model

import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.core.requireNotBlank
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "authors")
@Access(AccessType.FIELD)
class Author private constructor(
    @Column(nullable = false)
    val name: String,
): AbstractValueObject() {

    companion object {
        operator fun invoke(name: String = "Unknown"): Author {
            name.requireNotBlank("name")
            return Author(name)
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val books: MutableList<Book> = mutableListOf()

    fun addBook(book: Book) {
        if (books.add(book)) {
            book.author = this
        }
    }

    fun removeBook(book: Book) {
        if (books.remove(book)) {
            book.author = null
        }
    }

    override fun equalProperties(other: Any): Boolean =
        other is Author && name == other.name

    override fun hashCode(): Int = if (id != 0L) id.hashCode() else name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}
