package io.bluetape4k.io.grpc.examples.helloworld

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.io.grpc.inprocess.AbstractGrpcInprocessServer
import io.bluetape4k.logging.KLogging

/**
 * GreeterServer
 */
class GreeterServer private constructor(name: String): AbstractGrpcInprocessServer(name, GreeterService()) {

    companion object: KLogging() {
        operator fun invoke(name: String): GreeterServer {
            name.requireNotBlank("name")
            return GreeterServer(name)
        }
    }
}
