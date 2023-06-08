package io.bluetape4k.data.r2dbc.connection.init

import org.springframework.core.io.Resource
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

fun resourceDatabasePopulatorOf(resource: Resource): ResourceDatabasePopulator =
    ResourceDatabasePopulator(resource)

fun resourceDatabasePopulatorOf(vararg resources: Resource): ResourceDatabasePopulator =
    ResourceDatabasePopulator(*resources)

fun resourceDatabasePopulatorOf(
    continueOnError: Boolean,
    ignoreFailedDrops: Boolean,
    sqlScriptEncoding: String? = null,
    vararg resources: Resource,
): ResourceDatabasePopulator {
    return ResourceDatabasePopulator(continueOnError, ignoreFailedDrops, sqlScriptEncoding, *resources)
}
