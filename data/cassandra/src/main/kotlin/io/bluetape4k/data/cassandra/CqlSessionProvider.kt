package io.bluetape4k.data.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.utils.ShutdownQueue
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

object CqlSessionProvider: KLogging() {

    private val sessionCache = ConcurrentHashMap<String, CqlSession>()

    val DEFAULT_CONTACT_POINT = InetSocketAddress("localhost", 9042)
    const val DEFAULT_LOCAL_DATACENTER = "datacenter1"
    const val DEFAULT_KEYSPACE = "general"

    /**
     * [CqlSessionBuilder] 를 생성합니다.
     *
     * @param contactPoint    Scylla 서버 주소
     * @param localDatacenter LocalDataCenter 이름
     * @return [CqlSessionBuilder] 인스턴스
     */
    fun newCqlSessionBuilder(
        contactPoint: InetSocketAddress = CqlSessionProvider.DEFAULT_CONTACT_POINT,
        localDatacenter: String = CqlSessionProvider.DEFAULT_LOCAL_DATACENTER,
    ): CqlSessionBuilder {
        return CqlSessionBuilder()
            .addContactPoint(contactPoint)
            .withLocalDatacenter(localDatacenter)
    }

    /**
     * Keyspace 별로 [CqlSession]을 구분하여 사용하도록 합니다.
     *
     * @param keyspace  keyspace 명, null 이면 cql 에 keyspace 를 지정해주어야 합니다.
     * @param contactPoint Cassandra contact point
     * @param localDatacenter Cassandra local datacenter
     * @param builderSupplier [CqlSessionBuilder]를 제공하는 Supplier
     * @param initializer [CqlSessionBuilder]를 이용하여 설정하는 함수
     * @return `keyspace` 전용의 [CqlSession] 인스턴스
     */
    fun getOrCreateSession(
        keyspace: String = DEFAULT_KEYSPACE,
        builderSupplier: () -> CqlSessionBuilder = { newCqlSessionBuilder() },
        initializer: CqlSessionBuilder.() -> Unit,
    ): CqlSession {
        val closedSessions = sessionCache.filterValues { it.isClosed }
        closedSessions.forEach {
            sessionCache.remove(it.key)
        }

        return sessionCache.getOrPut(keyspace) {
            log.info { "Creating new CqlSession for $keyspace" }

            // keyspace가 없을 수 있으므로, adminSession으로 신규 keyspace를 생성하도록 합니다.
            builderSupplier().build().use { adminSession ->
                CassandraAdmin.createKeyspace(adminSession, keyspace)
            }

            builderSupplier()
                .withKeyspace(keyspace)
                .apply(initializer)
                .build()
                .apply {
                    ShutdownQueue.register(this)
                }
        }
    }
}
