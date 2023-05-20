package io.bluetape4k.workshop.redis.examples.repositories

import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.support.hashOf
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

/**
 * [Person] object stored inside a Redis `HASH`.
 *
 * Sample (key = persons:9b0ed8ee-14be-46ec-b5fa-79570aadb91d):
 *
 * ```
 * _class := example.springdata.redis.domain.Person
 * id := 9b0ed8ee-14be-46ec-b5fa-79570aadb91d
 * firstname := eddard
 * lastname := stark
 * gender := MALE
 * address.city := winterfell
 * address.country := the north
 * children.[0] := persons:41436096-aabe-42fa-bd5a-9a517fbf0260
 * children.[1] := persons:1973d8e7-fbd4-4f93-abab-a2e3a00b3f53
 * children.[2] := persons:440b24c6-ede2-495a-b765-2d8b8d6e3995
 * children.[3] := persons:85f0c1d1-cef6-40a4-b969-758ebb68dd7b
 * children.[4] := persons:73cb36e8-add9-4ec0-b5dd-d820e04f44f0
 * children.[5] := persons:9c2461aa-2ef2-469f-83a2-bd216df8357f
 * ```
 */
@RedisHash("persons")
class Person: AbstractValueObject() {

    companion object {
        @JvmStatic
        operator fun invoke(firstname: String, lastname: String, gender: Gender = Gender.UNKNOWN): Person {
            return Person().apply {
                this.firstname = firstname
                this.lastname = lastname
                this.gender = gender
            }
        }
    }


    /**
     * The [id] and [RedisHash#toString()] build up the [key] for the Redis [HASH].
     *
     * ```
     * RedisHash.value() + ":" + Person.id
     * //eg. persons:9b0ed8ee-14be-46ec-b5fa-79570aadb91d
     * ```
     *
     * **Note: empty [id] fields are automatically assigned during save operation.**
     */
    @Id
    var id: String? = null

    val identifier: String get() = id!!

    /**
     * Using [Indexed] marks the property as for indexing which uses Redis [SET] to keep track of
     * [ids] for objects with matching values.
     *
     * ```
     * RedisHash.value() + ":" + Field.getName() + ":" + Field.get(Object)
     * //eg. persons:firstname:eddard
     * ```
     */
    @Indexed
    var firstname: String = ""

    @Indexed
    var lastname: String = ""

    var gender: Gender = Gender.UNKNOWN

    /**
     * Since [Indexed] is used on [Address.city], index structures for `persons:address:city` are maintained.
     */
    var address: Address? = null

    /**
     * Using [Reference] allows to link to existing objects via their `key`.
     * The values stored in the objects `HASH` looks like:
     *
     * ```
     * children.[0] := persons:41436096-aabe-42fa-bd5a-9a517fbf0260
     * children.[1] := persons:1973d8e7-fbd4-4f93-abab-a2e3a00b3f53
     * children.[2] := persons:440b24c6-ede2-495a-b765-2d8b8d6e3995
     * ```
     */
    @Reference
    var children: MutableList<Person> = mutableListOf()
        private set


    override fun equalProperties(other: Any): Boolean {
        return other is Person &&
               id == other.id &&
               firstname == other.firstname &&
               lastname == other.lastname
    }

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun hashCode(): Int = id?.hashCode() ?: hashOf(firstname, lastname)

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("id", id)
            .add("firstname", firstname)
            .add("lastname", lastname)
    }
}
