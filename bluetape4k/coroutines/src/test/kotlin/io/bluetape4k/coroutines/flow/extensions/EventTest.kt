package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class EventTest: AbstractFlowTest() {

    companion object: KLogging()

    @Test
    fun `toString of Event Value`() {
        Event.Value(1).toString() shouldBeEqualTo "Event.Value(1)"
        Event.Value("Hello, World!").toString() shouldBeEqualTo "Event.Value(Hello, World!)"
    }

    @Test
    fun `equals and hashCode of Event Value`() {
        val list1 = listOf(1, 2, 3)
        val list2 = listOf(1, 2, 3)
        list1 shouldBeEqualTo list2

        val ev1 = Event.Value(list1)
        val ev2 = Event.Value(list2)
        ev1 shouldBeEqualTo ev2
        ev1.hashCode() shouldBeEqualTo ev2.hashCode()
    }

    @Test
    fun `toString of Event Error`() {
        val error = RuntimeException("Boom!")
        Event.Error(error).toString() shouldBeEqualTo "Event.Error($error)"
    }

    @Test
    fun `equals and hashCode of Event Error`() {
        val e = RuntimeException("Boom!")

        Event.Error(e) shouldBeEqualTo Event.Error(e)
        Event.Error(e).hashCode() shouldBeEqualTo Event.Error(e).hashCode()

        e.hashCode() shouldBeEqualTo Event.Error(e).hashCode()
    }

    @Test
    fun `toString of Event Complete`() {
        Event.Complete.toString() shouldBeEqualTo "Event.Complete"
    }

    @Test
    fun `map Event`() {

        Event.Value(1).map { it + 1 } shouldBeEqualTo Event.Value(2)

        assertFailsWith<RuntimeException> {
            Event.Value(1).map { throw RuntimeException("Boom!") }
        }.message shouldBeEqualTo "Boom!"

        val e2: Event<Int> = Event.Error(RuntimeException("1"))
        e2.map { it + 1 } shouldBeEqualTo e2

        val completeEvent: Event<Int> = Event.Complete
        completeEvent.map { it + 1 } shouldBeEqualTo completeEvent
    }

    @Test
    fun `flatMap Event`() {
        Event.Value(1).flatMap { Event.Value(it + 1) } shouldBeEqualTo Event.Value(2)

        Event.Value(1).flatMap { Event.Complete } shouldBeEqualTo Event.Complete

        val ex = RuntimeException("Boom!")
        Event.Value(1).flatMap { Event.Error(ex) } shouldBeEqualTo Event.Error(ex)

        assertFailsWith<RuntimeException> {
            Event.Value(1).flatMap<Int, String> { throw RuntimeException("error") }
        }.message shouldBeEqualTo "error"

        val errorEvent: Event<Int> = Event.Error(RuntimeException("1"))
        errorEvent.flatMap { Event.Value(it + 1) } shouldBeEqualTo errorEvent

        val complete: Event<Int> = Event.Complete
        complete.flatMap { Event.Value(it + 1) } shouldBeEqualTo complete
    }

    @Test
    fun `valueOrNull for Event`() {
        Event.Value(1).valueOrNull() shouldBeEqualTo 1
        Event.Error(RuntimeException("Boom!")).valueOrNull<Int>().shouldBeNull()
        Event.Complete.valueOrNull<Int>().shouldBeNull()
    }

    @Test
    fun `valueOrDefault for Event`() {
        val defaultValue = 2

        Event.Value(1).valueOrDefault(defaultValue) shouldBeEqualTo 1
        Event.Error(RuntimeException("Boom!")).valueOrDefault(defaultValue) shouldBeEqualTo defaultValue
        Event.Complete.valueOrDefault(defaultValue) shouldBeEqualTo defaultValue
    }

    @Test
    fun `valueOrThrow for Event`() {
        Event.Value(1).valueOrThrow() shouldBeEqualTo 1
        assertFailsWith<RuntimeException> {
            Event.Error(RuntimeException("1")).valueOrThrow()
        }.message shouldBeEqualTo "1"

        assertFailsWith<NoSuchElementException> {
            Event.Complete.valueOrThrow()
        }.message shouldBeEqualTo "Event.Complete has no value!"
    }

    @Test
    fun `valueOrElse for Event`() {
        val defaultValue = 2

        Event.Value(1).valueOrElse { defaultValue } shouldBeEqualTo 1
        Event.Error(RuntimeException("Boom!")).valueOrElse { defaultValue } shouldBeEqualTo defaultValue
        Event.Complete.valueOrElse { defaultValue } shouldBeEqualTo defaultValue
    }

    @Test
    fun `errorOrNull for Event`() {
        val ex = RuntimeException("Boom!")
        Event.Value(1).errorOrNull().shouldBeNull()
        Event.Error(ex).errorOrNull() shouldBeEqualTo ex
        Event.Complete.errorOrNull().shouldBeNull()
    }

    @Test
    fun `errorOrThrow for Event`() {
        val ex = RuntimeException("Boom!")

        assertFailsWith<NoSuchElementException> {
            Event.Value(1).errorOrThrow()
        }.message shouldBeEqualTo "Event.Value(1) has no error!"

        Event.Error(ex).errorOrThrow() shouldBeEqualTo ex

        assertFailsWith<NoSuchElementException> {
            Event.Complete.errorOrThrow()
        }.message shouldBeEqualTo "Event.Complete has no error!"
    }
}
