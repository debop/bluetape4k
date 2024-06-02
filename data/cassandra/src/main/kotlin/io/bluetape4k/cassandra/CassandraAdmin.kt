package io.bluetape4k.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.Version
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import io.bluetape4k.core.LibraryName
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.info
import io.bluetape4k.support.requireNotBlank

object CassandraAdmin: KLogging() {

    private const val DEFAULT_KEYSPACE = LibraryName
    private const val DEFAULT_REPLICATION_FACTOR = 1

    fun createKeyspace(
        session: CqlSession,
        keyspace: String = CassandraAdmin.DEFAULT_KEYSPACE,
        replicationFactor: Int = CassandraAdmin.DEFAULT_REPLICATION_FACTOR,
    ): Boolean {
        keyspace.requireNotBlank("keySpace")

        val stmt = SchemaBuilder.createKeyspace(keyspace)
            .ifNotExists()
            .withSimpleStrategy(replicationFactor)
            .build()

        return session.execute(stmt).wasApplied().apply {
            log.info { "Create Keyspace[$keyspace], replicationFactor[$replicationFactor] wasApplied [$this]" }
        }
    }

    fun dropKeyspace(session: CqlSession, keyspace: String): Boolean {
        keyspace.requireNotBlank("keySpace")

        val stmt = SchemaBuilder.dropKeyspace(keyspace).ifExists().build()

        return session.execute(stmt).wasApplied().apply {
            log.info { "Drop Keyspace[$keyspace] wasApplied [$this]" }
        }
    }

    fun getReleaseVersion(session: CqlSession): Version? {
        val stmt = QueryBuilder.selectFrom("system", "local")
            .column("release_version")
            .build()

        val row = session.execute(stmt).one()
        val releaseVersion = row?.getString(0)
        log.debug { "Cassandra release version=$releaseVersion" }

        return Version.parse(releaseVersion)
    }
}
