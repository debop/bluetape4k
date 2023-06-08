package io.bluetape4k.data.r2dbc.connection.init

import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.DatabasePopulator

fun compositeDatabasePopulatorOf(vararg populators: DatabasePopulator): CompositeDatabasePopulator =
    CompositeDatabasePopulator(*populators)

fun compositeDatabasePopulatorOf(populators: Collection<DatabasePopulator>): CompositeDatabasePopulator =
    CompositeDatabasePopulator(populators)
