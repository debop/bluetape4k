package io.bluetape4k.vertx.sqlclient.mybatis

import org.mybatis.dynamic.sql.delete.DeleteModel
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider
import org.mybatis.dynamic.sql.insert.*
import org.mybatis.dynamic.sql.insert.render.*
import org.mybatis.dynamic.sql.render.TableAliasCalculator
import org.mybatis.dynamic.sql.select.SelectModel
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider
import org.mybatis.dynamic.sql.update.UpdateModel
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider
import org.mybatis.dynamic.sql.where.WhereModel
import org.mybatis.dynamic.sql.where.render.WhereClauseProvider
import java.util.*


fun SelectModel.renderForVertx(): SelectStatementProvider =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun DeleteModel.renderForVertx(): DeleteStatementProvider =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun <T> InsertModel<T>.renderForVertx(): InsertStatementProvider<T> =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun GeneralInsertModel.renderForVertx(): GeneralInsertStatementProvider =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun <T> BatchInsertModel<T>.renderForVertx(): BatchInsert<T> =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun InsertSelectModel.renderForVertx(): InsertSelectStatementProvider =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun <T> MultiRowInsertModel<T>.renderForVertx(): MultiRowInsertStatementProvider<T> =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun UpdateModel.renderForVertx(): UpdateStatementProvider =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun WhereModel.renderForVertx(): Optional<WhereClauseProvider> =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY)

fun WhereModel.renderForVertx(tableAliasCalculator: TableAliasCalculator): Optional<WhereClauseProvider> =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY, tableAliasCalculator)

fun WhereModel.renderForVertx(parameterName: String): Optional<WhereClauseProvider> =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY, parameterName)

fun WhereModel.renderForVertx(
    tableAliasCalculator: TableAliasCalculator,
    parameterName: String
): Optional<WhereClauseProvider> =
    render(VERTX_SQL_CLIENT_RENDERING_STRATEGY, tableAliasCalculator, parameterName)
