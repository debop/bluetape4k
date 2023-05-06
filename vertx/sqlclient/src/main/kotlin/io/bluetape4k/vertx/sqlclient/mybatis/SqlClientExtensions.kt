package io.bluetape4k.vertx.sqlclient.mybatis

import io.bluetape4k.vertx.sqlclient.SqlLogger
import io.bluetape4k.vertx.sqlclient.templates.LONG_ROW_MAPPER
import io.bluetape4k.vertx.sqlclient.templates.toParameters
import io.bluetape4k.vertx.sqlclient.templates.tupleMapperOfRecord
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.SqlResult
import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.SqlTemplate
import org.mybatis.dynamic.sql.BasicColumn
import org.mybatis.dynamic.sql.SqlBuilder
import org.mybatis.dynamic.sql.SqlTable
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider
import org.mybatis.dynamic.sql.insert.render.BatchInsert
import org.mybatis.dynamic.sql.insert.render.GeneralInsertStatementProvider
import org.mybatis.dynamic.sql.insert.render.InsertSelectStatementProvider
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider
import org.mybatis.dynamic.sql.util.kotlin.CountCompleter
import org.mybatis.dynamic.sql.util.kotlin.DeleteCompleter
import org.mybatis.dynamic.sql.util.kotlin.GeneralInsertCompleter
import org.mybatis.dynamic.sql.util.kotlin.InsertSelectCompleter
import org.mybatis.dynamic.sql.util.kotlin.KotlinBatchInsertBuilder
import org.mybatis.dynamic.sql.util.kotlin.KotlinCountBuilder
import org.mybatis.dynamic.sql.util.kotlin.KotlinDeleteBuilder
import org.mybatis.dynamic.sql.util.kotlin.KotlinInsertBuilder
import org.mybatis.dynamic.sql.util.kotlin.KotlinInsertCompleter
import org.mybatis.dynamic.sql.util.kotlin.KotlinMultiRowInsertBuilder
import org.mybatis.dynamic.sql.util.kotlin.KotlinSelectBuilder
import org.mybatis.dynamic.sql.util.kotlin.SelectCompleter
import org.mybatis.dynamic.sql.util.kotlin.UpdateCompleter

//
// [SqlClient]를 Mybatis Dynamic SQL 을 이용하여 작업할 수 있도록 해주는 확장 함수들입니다.
//

//
// COUNT
//
suspend fun SqlClient.count(
    countProvider: SelectStatementProvider,
): Long {
    SqlLogger.logSQL(countProvider.selectStatement, countProvider.parameters)

    return SqlTemplate.forQuery(this, countProvider.selectStatement)
        .mapTo(LONG_ROW_MAPPER)
        .execute(countProvider.parameters)
        .await()
        .firstOrNull() ?: -1L
}

suspend fun SqlClient.count(
    column: BasicColumn,
    completer: CountCompleter,
): Long {
    val model = KotlinCountBuilder(SqlBuilder.countColumn(column)).apply(completer).build()
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return count(provider)
}

suspend fun SqlClient.countDistinct(
    column: BasicColumn,
    completer: CountCompleter,
): Long {
    val model = KotlinCountBuilder(SqlBuilder.countDistinctColumn(column)).apply(completer).build()
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return count(provider)
}

suspend fun SqlClient.countFrom(table: SqlTable, completer: CountCompleter): Long {
    val model =
        KotlinCountBuilder(SqlBuilder.countColumn(SqlBuilder.constant<Long>("*"))).from(table).apply(completer)
            .build()
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return count(provider)
}

//
// DELETE
//

suspend fun SqlClient.delete(deleteProvider: DeleteStatementProvider): SqlResult<Void> {
    SqlLogger.logSQL(deleteProvider.deleteStatement, deleteProvider.parameters)

    return SqlTemplate.forUpdate(this, deleteProvider.deleteStatement)
        .execute(deleteProvider.parameters)
        .await()
}

suspend fun SqlClient.deleteFrom(table: SqlTable, completer: DeleteCompleter): SqlResult<Void> {
    val model = KotlinDeleteBuilder(SqlBuilder.deleteFrom(table)).apply(completer).build()
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return delete(provider)
}

//
// BATCH INSERT
//
/**
 *
 * [Vertx SqlClient Templates executeBatch](https://vertx.io/docs/vertx-sql-client-templates/java/#_parameters_mapping)
 * @param T
 * @param batchInsert
 * @return
 */
suspend fun <T: Any> SqlClient.insertBatch(batchInsert: BatchInsert<T>): SqlResult<Void> {
    SqlLogger.logSQL(batchInsert.insertStatementSQL, batchInsert.records)

    return SqlTemplate.forUpdate(this, batchInsert.insertStatementSQL)
        .mapFrom<T>(tupleMapperOfRecord())
        .executeBatch(batchInsert.records)
        .await()
}

suspend fun <T: Any> SqlClient.insertBatch(
    vararg records: T,
    completer: KotlinBatchInsertBuilder<T>.() -> Unit,
): SqlResult<Void> {
    return insertBatch(records.asList(), completer)
}

suspend fun <T: Any> SqlClient.insertBatch(
    records: List<T>,
    completer: KotlinBatchInsertBuilder<T>.() -> Unit,
): SqlResult<Void> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.insertBatch(records, completer)
    val batchInsert = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return insertBatch(batchInsert)
}

//
// INSERT
//

suspend fun <T: Any> SqlClient.insert(insertProvider: InsertStatementProvider<T>): SqlResult<Void> {
    SqlLogger.logSQL(insertProvider.insertStatement, insertProvider.row)

    return SqlTemplate.forUpdate(this, insertProvider.insertStatement)
        .mapFrom(tupleMapperOfRecord<T>())
        .execute(insertProvider.row)
        .await()
}

suspend fun <T: Any> SqlClient.insert(entity: T, completer: KotlinInsertCompleter<T>): SqlResult<Void> {
    val model = KotlinInsertBuilder(entity).apply(completer).build()
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return insert(provider)
}

suspend fun SqlClient.generalInsert(insertProvider: GeneralInsertStatementProvider): SqlResult<Void> {
    SqlLogger.logSQL(insertProvider.insertStatement, insertProvider.parameters)

    return SqlTemplate.forUpdate(this, insertProvider.insertStatement)
        .execute(insertProvider.parameters)
        .await()
}

suspend fun SqlClient.generalInsert(table: SqlTable, completer: GeneralInsertCompleter): SqlResult<Void> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.insertInto(table, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return generalInsert(provider)
}

//
// INSERT MULTIPLE
//
suspend fun <T: Any> SqlClient.insertMultiple(
    vararg records: T,
    completer: KotlinMultiRowInsertBuilder<T>.() -> Unit,
): SqlResult<Void> {
    return insertMultiple(records.asList(), completer)
}

suspend fun <T: Any> SqlClient.insertMultiple(
    records: List<T>,
    completer: KotlinMultiRowInsertBuilder<T>.() -> Unit,
): SqlResult<Void> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.insertMultiple(records, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

    return insertMultiple(provider)
}

suspend fun <T: Any> SqlClient.insertMultiple(insertProvider: MultiRowInsertStatementProvider<T>): SqlResult<Void> {
    SqlLogger.logSQL(insertProvider.insertStatement, insertProvider.records)

    return SqlTemplate.forUpdate(this, insertProvider.insertStatement)
        .execute(insertProvider.records.toParameters())
        .await()
}

//
// INSERT SELECT
//
suspend fun SqlClient.insertSelect(provider: InsertSelectStatementProvider): SqlResult<Void> {
    SqlLogger.logSQL(provider.insertStatement, provider.parameters)

    return SqlTemplate.forUpdate(this, provider.insertStatement)
        .execute(provider.parameters)
        .await()
}

suspend fun SqlClient.insertSelect(completer: InsertSelectCompleter): SqlResult<Void> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.insertSelect(completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return insertSelect(provider)
}

//
// SELECT
//
suspend fun SqlClient.select(provider: SelectStatementProvider): RowSet<Row> {
    SqlLogger.logSQL(provider.selectStatement, provider.parameters)

    return SqlTemplate.forQuery(this, provider.selectStatement)
        .execute(provider.parameters)
        .await()
}

suspend fun SqlClient.select(vararg columns: BasicColumn, completer: SelectCompleter): RowSet<Row> {
    return select(columns.asList(), completer)
}

suspend fun SqlClient.select(columns: List<BasicColumn>, completer: SelectCompleter): RowSet<Row> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.select(columns, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return select(provider)
}

suspend fun <T: Any> SqlClient.select(provider: SelectStatementProvider, rowMapper: RowMapper<T>): RowSet<T> {
    SqlLogger.logSQL(provider.selectStatement, provider.parameters)

    return SqlTemplate.forQuery(this, provider.selectStatement)
        .mapTo(rowMapper)
        .execute(provider.parameters)
        .await()
}

suspend fun <T: Any> SqlClient.select(
    columns: List<BasicColumn>,
    rowMapper: RowMapper<T>,
    completer: SelectCompleter,
): RowSet<T> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.select(columns, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

    return select(provider, rowMapper)
}

suspend inline fun <reified T: Any> SqlClient.selectAs(provider: SelectStatementProvider): RowSet<T> {
    SqlLogger.logSQL(provider.selectStatement, provider.parameters)

    return SqlTemplate.forQuery(this, provider.selectStatement)
        .mapTo(T::class.java)
        .execute(provider.parameters)
        .await()
}

suspend inline fun <reified T: Any> SqlClient.selectAs(
    columns: List<BasicColumn>,
    noinline completer: KotlinSelectBuilder.() -> Unit,
): RowSet<T> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.select(columns, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

    return selectAs(provider)
}

internal suspend fun SqlClient.selectDistinct(provider: SelectStatementProvider): RowSet<Row> {
    SqlLogger.logSQL(provider.selectStatement, provider.parameters)

    return SqlTemplate.forQuery(this, provider.selectStatement)
        .execute(provider.parameters)
        .await()
}

suspend fun SqlClient.selectDistinct(vararg columns: BasicColumn, completer: SelectCompleter): RowSet<Row> {
    return selectDistinct(columns.asList(), completer)
}

suspend fun SqlClient.selectDistinct(columns: List<BasicColumn>, completer: SelectCompleter): RowSet<Row> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.selectDistinct(columns, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return selectDistinct(provider)
}

suspend inline fun <reified T: Any> SqlClient.selectListAs(provider: SelectStatementProvider): List<T> {
    return SqlTemplate.forQuery(this, provider.selectStatement)
        .mapTo(T::class.java)
        .execute(provider.parameters)
        .await()
        .map { it }
}

suspend fun <T> SqlClient.selectList(provider: SelectStatementProvider, mapper: RowMapper<T>): List<T> {
    SqlLogger.logSQL(provider.selectStatement, provider.parameters)

    return SqlTemplate.forQuery(this, provider.selectStatement)
        .mapTo(mapper)
        .execute(provider.parameters)
        .await()
        .map { it }
}

suspend fun <T: Any> SqlClient.selectList(
    columns: List<BasicColumn>,
    rowMapper: RowMapper<T>,
    completer: SelectCompleter,
): List<T> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.selectDistinct(columns, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return selectList(provider, rowMapper)
}

suspend fun SqlClient.selectOne(provider: SelectStatementProvider): Row? {
    SqlLogger.logSQL(provider.selectStatement, provider.parameters)

    return SqlTemplate.forQuery(this, provider.selectStatement)
        .execute(provider.parameters)
        .await()
        .firstOrNull()
}

suspend fun SqlClient.selectOne(vararg columns: BasicColumn, completer: SelectCompleter): Row? {
    return selectOne(columns.asList(), completer)
}

suspend fun SqlClient.selectOne(columns: List<BasicColumn>, completer: SelectCompleter): Row? {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.select(columns, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return selectOne(provider)
}

suspend fun <T: Any> SqlClient.selectOne(provider: SelectStatementProvider, mapper: RowMapper<T>): T? {
    SqlLogger.logSQL(provider.selectStatement, provider.parameters)

    return SqlTemplate.forQuery(this, provider.selectStatement)
        .mapTo(mapper)
        .execute(provider.parameters)
        .await()
        .firstOrNull()
}

suspend inline fun <reified T: Any> SqlClient.selectOneAs(provider: SelectStatementProvider): T? {
    // logSQL(provider.selectStatement, provider.parameters)
    return SqlTemplate.forQuery(this, provider.selectStatement)
        .mapTo(T::class.java)
        .execute(provider.parameters)
        .await()
        .firstOrNull()
}

suspend fun <T: Any> SqlClient.selectOne(
    columns: List<BasicColumn>,
    rowMapper: RowMapper<T>,
    completer: SelectCompleter,
): T? {
    return selectOne(columns, completer)?.let { rowMapper.map(it) }
}

//
// UPDATE
//
suspend fun SqlClient.update(provider: UpdateStatementProvider): SqlResult<Void> {
    SqlLogger.logSQL(provider.updateStatement, provider.parameters)

    return SqlTemplate.forUpdate(this, provider.updateStatement)
        .execute(provider.parameters)
        .await()
}

suspend fun SqlClient.update(table: SqlTable, completer: UpdateCompleter): SqlResult<Void> {
    val model = org.mybatis.dynamic.sql.util.kotlin.model.update(table, completer)
    val provider = model.render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)
    return update(provider)
}
