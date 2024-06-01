package io.bluetape4k.jackson.text.datasources

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.io.toInputStream
import io.bluetape4k.jackson.text.AbstractJacksonTextTest
import io.bluetape4k.jackson.text.properties.JacksonProps
import io.bluetape4k.jackson.text.yaml.JacksonYaml
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import javax.sql.DataSource

class ParseNamedDataSourcePropertiesTest: AbstractJacksonTextTest() {

    companion object: KLogging()

    private val propsMapper: JavaPropsMapper by lazy { JacksonProps.defaultPropsMapper }
    private val propsFactory: JavaPropsFactory by lazy { JacksonProps.defaultPropsFactory }
    private val objectMapper: ObjectMapper by lazy { JacksonProps.defaultObjectMapper }

    private val yamlMapper: YAMLMapper by lazy { JacksonYaml.defaultYamlMapper }


    val default = Dbcp2DataSourceProperty(
        driverClassName = "mysql",
        url = "jdbc:mysql://localhost:3306/test",
        username = "sa",
        password = "password",

        maxTotal = 50,
        maxIdle = 40,
        minIdle = 10,
        maxWaitMillis = null, // 60_000,
        lifo = true
    )

    val read = HikariDataSourceProperty(
        driverClassName = "mariadb",
        url = "jdbc:mysql://localhost:3307/test",
        username = "sa",
        password = "password",
        connectionTimeout = 5_000,
        idleTimeout = null, // 60_000L,
        maxLifetime = 60_000L,
        properties = listOf("cachePropStmts=true", "prepStmtCacheSize=250", "propStmtCacheSqlLimit=2048")
    )

    @Nested
    inner class ParseProperties {

        private val properties = """
        |kommons.datasources.default.connectionPool=dbcp2
        |kommons.datasources.default.driverClassName=mysql
        |kommons.datasources.default.url=jdbc:mysql://localhost:3306/test
        |kommons.datasources.default.username=sa
        |kommons.datasources.default.password=password
        |kommons.datasources.default.maxTotal=50
        |kommons.datasources.default.maxIdle=40
        |kommons.datasources.default.minIdle=10
        |kommons.datasources.default.maxWaitMillis=
        |kommons.datasources.default.lifo=true
        |kommons.datasources.default.connectionProperties=
        |kommons.datasources.read.connectionPool=hikari
        |kommons.datasources.read.driverClassName=mariadb
        |kommons.datasources.read.url=jdbc:mysql://localhost:3307/test
        |kommons.datasources.read.username=sa
        |kommons.datasources.read.password=password
        |kommons.datasources.read.connectionTimeout=5000
        |kommons.datasources.read.idleTimeout=
        |kommons.datasources.read.maxLifetime=60000
        |kommons.datasources.read.properties.1=cachePropStmts=true
        |kommons.datasources.read.properties.2=prepStmtCacheSize=250
        |kommons.datasources.read.properties.3=propStmtCacheSqlLimit=2048
        |
        """.trimMargin()


        @Disabled("PropsMapper 에 버그가 있다. 미지정 속성은 null 이 되어야 하는데, maxWaitMillis=0 으로 지정된다.")
        @Test
        fun `generate datasource properties to properties format and parse`() {
            val kommons = kommonsProperty(mapOf("default" to default, "read" to read))
            val root = RootProperty(kommons)

            val propertyString = propsMapper.writeValueAsString(root) // writeValueAsProperties
            log.debug { "properties=\n$propertyString\n------" }
            propertyString shouldBeEqualTo properties

            val parsedRoot = propsMapper.readValue<RootProperty>(propertyString)
            parsedRoot.kommons.datasources.size shouldBeEqualTo 2

            val parsedDefault = parsedRoot.kommons.datasources["default"]
            val parsedRead = parsedRoot.kommons.datasources["read"]

            parsedDefault shouldBeEqualTo default
            parsedRead shouldBeEqualTo read
        }

        @Disabled("PropsMapper 에 버그가 있다. 미지정 속성은 null 이 되어야 하는데, maxWaitMillis=0 으로 지정된다.")
        fun `generate datasource properties to properties and parse`() {
            val kommons = kommonsProperty(mapOf("default" to default, "read" to read))
            val root = RootProperty(kommons)

            val properties = propsMapper.writeValueAsProperties(root)
            log.debug { "properties=\n$properties\n------" }
            properties shouldBeEqualTo Properties().also { it.load(this.properties.toInputStream()) }

            val parsedRoot = propsMapper.readPropertiesAs(properties, RootProperty::class.java)
            parsedRoot.kommons.datasources.size shouldBeEqualTo 2

            val parsedDefault = parsedRoot.kommons.datasources["default"]
            val parsedRead = parsedRoot.kommons.datasources["read"]

            parsedDefault shouldBeEqualTo default
            parsedRead shouldBeEqualTo read
        }
    }

    @Nested
    inner class ParseYaml {
        val yaml = """
            |---
            |kommons:
            |  datasources:
            |    default: !<dbcp2>
            |      driverClassName: "mysql"
            |      url: "jdbc:mysql://localhost:3306/test"
            |      username: "sa"
            |      password: "password"
            |      maxTotal: 50
            |      maxIdle: 40
            |      minIdle: 10
            |      maxWaitMillis: null
            |      lifo: true
            |      connectionProperties: ""
            |    read: !<hikari>
            |      driverClassName: "mariadb"
            |      url: "jdbc:mysql://localhost:3307/test"
            |      username: "sa"
            |      password: "password"
            |      connectionTimeout: 5000
            |      idleTimeout: null
            |      maxLifetime: 60000
            |      properties:
            |      - "cachePropStmts=true"
            |      - "prepStmtCacheSize=250"
            |      - "propStmtCacheSqlLimit=2048"
            |
            """.trimMargin()

        @Test
        fun `generate datasource properties to yaml format and parse`() {
            val property = kommonsProperty(mapOf("default" to default, "read" to read))
            val root = RootProperty(property)

            val yamlString = yamlMapper.writeValueAsString(root)

            log.debug { "yaml=\n$yamlString\n------" }
            yamlString shouldBeEqualTo yaml

            val parsedRoot = yamlMapper.readValue<RootProperty>(yamlString)

            parsedRoot.shouldNotBeNull()
            parsedRoot.kommons.datasources.size shouldBeEqualTo 2

            val parsedDefault = parsedRoot.kommons.datasources["default"]
            val parsedRead = parsedRoot.kommons.datasources["read"]

            parsedDefault shouldBeEqualTo default
            parsedRead shouldBeEqualTo read
        }

        @Test
        fun `yaml data type can just string`() {
            val parsedRoot = yamlMapper.readValue<RootProperty>(yaml)

            parsedRoot.shouldNotBeNull()
            parsedRoot.kommons.datasources.size shouldBeEqualTo 2

            val parsedDefault = parsedRoot.kommons.datasources["default"]
            val parsedRead = parsedRoot.kommons.datasources["read"]

            parsedDefault shouldBeEqualTo default
            parsedRead shouldBeEqualTo read
        }
    }


    data class RootProperty(val kommons: kommonsProperty)
    data class kommonsProperty(val datasources: Map<String, DataSourceProperty<out DataSource>> = emptyMap())

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "connectionPool")
    @JsonSubTypes(
        JsonSubTypes.Type(value = Dbcp2DataSourceProperty::class),
        JsonSubTypes.Type(value = HikariDataSourceProperty::class)
    )
    interface DataSourceProperty<DS: DataSource> {
        val driverClassName: String
        val url: String
        val username: String?
        val password: String?
    }

    @JsonTypeName(value = "dbcp2")
    data class Dbcp2DataSourceProperty(
        override val driverClassName: String,
        override val url: String,
        override val username: String?,
        override val password: String?,

        val maxTotal: Int?,
        val maxIdle: Int?,
        val minIdle: Int?,
        val maxWaitMillis: Int? = null,

        val lifo: Boolean?,

        // Properties에서 값이 없는 경우에는 empty string으로 지정된다. Parsing 후에는 empty string으로 지정된다.
        var connectionProperties: String = "",

        ): DataSourceProperty<DataSource>

    @JsonTypeName("hikari")
    data class HikariDataSourceProperty(

        override val driverClassName: String,
        override val url: String,
        override val username: String?,
        override val password: String?,

        val connectionTimeout: Int?,
        val idleTimeout: Long?,
        val maxLifetime: Long?,

        val properties: List<String> = emptyList(),
    ): DataSourceProperty<DataSource>

    enum class ConnectionPoolType {
        DBPC2,
        HIKARI,
        MARIADB,
        TOMCAT
    }

}
