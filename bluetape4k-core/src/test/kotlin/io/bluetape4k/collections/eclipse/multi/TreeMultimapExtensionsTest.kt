package io.bluetape4k.collections.eclipse.multi

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class TreeMultimapExtensionsTest {

    companion object : KLogging()

    data class User(val name: String, val age: Int)

    val users = listOf(
        User("alex", 11),
        User("bob", 22),
        User("sam", 22),
        User("jane", 11),
        User("rex", 44)
    )


    @Test
    fun `Key로 정렬한 TreeMultimap 만들기`() {
        val userGroup = users.toTreeMultimap { it.age }

        userGroup.size shouldBeEqualTo 3
        userGroup.valueSize() shouldBeEqualTo users.size
        userGroup.first.map { it.name } shouldBeEqualTo listOf("alex", "jane")
        userGroup.last.map { it.name } shouldBeEqualTo listOf("rex")
    }

    @Test
    fun `Key로 역정렬한 TreeMultimap 만들기`() {
        val reverseOrder = Comparator.reverseOrder<Int>()
        val userGroup = users.toTreeMultimap(reverseOrder) { it.age }

        userGroup.size shouldBeEqualTo 3
        userGroup.valueSize() shouldBeEqualTo users.size
        userGroup.first.map { it.name } shouldBeEqualTo listOf("rex")
        userGroup.last.map { it.name } shouldBeEqualTo listOf("alex", "jane")
    }
}
