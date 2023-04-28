package io.bluetape4k.spring.cassandra

import io.bluetape4k.spring.cassandra.domain.model.AllPossibleTypes
import io.bluetape4k.testcontainers.storage.Cassandra4Server
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration
import org.springframework.data.cassandra.config.SchemaAction

@EntityScan(basePackageClasses = [AllPossibleTypes::class])
abstract class AbstractReactiveCassandraTestConfiguration: AbstractReactiveCassandraConfiguration() {

    // NOTE: 테스트 시에는 testcontainers 를 실행하기 위해 ScyllaServer.Launcher 작업을 미리 수행해야 합니다.
    //
    companion object {
        const val DEFAULT_KEYSPACE = "examples"

        val server by lazy { Cassandra4Server.Launcher.cassandra4 }

        init {
            // default keyspace 를 재생성합니다.
            Cassandra4Server.Launcher.recreateKeyspace(DEFAULT_KEYSPACE)
        }
    }

    override fun getPort(): Int = Cassandra4Server.Launcher.cassandra4.port

    override fun getContactPoints(): String = Cassandra4Server.Launcher.cassandra4.host

    override fun getKeyspaceName(): String = DEFAULT_KEYSPACE

    override fun getSchemaAction(): SchemaAction = SchemaAction.RECREATE

    // NOTE: 테스트 대상 Entity가 있는 Package를 지정하거나 @EntityScan 을 사용하세요
    // override fun getEntityBasePackages(): Array<String> = arrayOf(AllPossibleTypes::class.packageName)
}
