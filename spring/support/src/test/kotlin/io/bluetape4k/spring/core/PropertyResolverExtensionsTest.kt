package io.bluetape4k.spring.core

import io.bluetape4k.logging.KLogging
import java.util.Properties
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertyResolver
import org.springframework.core.env.PropertySourcesPropertyResolver
import kotlin.test.assertFailsWith

class PropertyResolverExtensionsTest {

    companion object: KLogging()

    private lateinit var testProperties: Properties
    private lateinit var propertySources: MutablePropertySources
    private lateinit var propertyResolver: PropertyResolver

    @BeforeEach
    fun setup() {
        testProperties = Properties()
        propertySources = MutablePropertySources().apply {
            addFirst(PropertiesPropertySource("testProperties", testProperties))
        }
        propertyResolver = PropertySourcesPropertyResolver(propertySources)
    }

    @Test
    fun `get property values`() {
        propertyResolver["key"].shouldBeNull()
        propertyResolver["num"].shouldBeNull()
        propertyResolver["enabled"].shouldBeNull()

        testProperties["foo"] = "bar"
        testProperties["num"] = 5
        testProperties["enabled"] = true

        propertyResolver["foo"] shouldBeEqualTo "bar"
        propertyResolver["num", Int::class] shouldBeEqualTo 5
        propertyResolver["enabled", Boolean::class] shouldBeEqualTo true
    }

    @Test
    fun `get property value with default value`() {
        propertyResolver["foo"].shouldBeNull()
        propertyResolver["num", Int::class].shouldBeNull()
        propertyResolver["enabled", Boolean::class].shouldBeNull()

        propertyResolver["foo", "myDefault"] shouldBeEqualTo "myDefault"
        propertyResolver["num", Int::class, 1] shouldBeEqualTo 1
        propertyResolver["enabled", Boolean::class, false] shouldBeEqualTo false


        testProperties["foo"] = "bar"
        testProperties["num"] = 5
        testProperties["enabled"] = true

        propertyResolver["foo", "myDefault"] shouldBeEqualTo "bar"
        propertyResolver["num", Int::class, 1] shouldBeEqualTo 5
        propertyResolver["enabled", Boolean::class, false] shouldBeEqualTo true
    }

    @Test
    fun `get reuired property`() {
        assertFailsWith<IllegalStateException> {
            propertyResolver.getRequiredProperty("not-exists", String::class)
        }

        testProperties["required.key"] = "required.value"
        propertyResolver.getRequiredProperty("required.key", String::class) shouldBeEqualTo "required.value"
    }
}
