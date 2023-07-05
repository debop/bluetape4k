package io.bluetape4k.data.hibernate.mapping.localized

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import io.bluetape4k.data.hibernate.model.JpaLocalizedEntity
import io.bluetape4k.support.hashOf
import jakarta.persistence.*
import org.hibernate.annotations.Cascade
import java.util.*

@Entity(name = "travel_content")
@Access(AccessType.FIELD)
class TravelContent(
    var title: String,
    var contents: String,
    var creator: String,
): IntJpaEntity(), JpaLocalizedEntity<LocalTravelContent> {

    @CollectionTable(name = "travel_content_locale_map", joinColumns = [JoinColumn(name = "travel_content_id")])
    @MapKeyColumn(name = "locale_key", length = 256, nullable = false, unique = true)
    @ElementCollection(targetClass = LocalTravelContent::class)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    override val localeMap: MutableMap<Locale, LocalTravelContent> = hashMapOf()

    override fun createDefaultLocalizedValue(): LocalTravelContent {
        return LocalTravelContent(this.title, this.contents)
    }

    override fun equalProperties(other: Any): Boolean {
        return other is TravelContent &&
                title == other.title &&
                contents == other.contents &&
                creator == other.creator
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: hashOf(title, contents, creator)
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("title", title)
            .add("contents", contents)
            .add("creator", creator)
    }
}

@Embeddable
data class LocalTravelContent(
    var title: String,
    var content: String,
): JpaLocalizedEntity.LocalizedValue
