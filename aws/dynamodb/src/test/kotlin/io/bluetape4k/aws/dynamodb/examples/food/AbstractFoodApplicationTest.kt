package io.bluetape4k.aws.dynamodb.examples.food

import io.bluetape4k.aws.dynamodb.AbstractDynamodbTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.idgenerators.snowflake.GlobalSnowflake

abstract class AbstractFoodApplicationTest: AbstractDynamodbTest() {

    companion object: KLogging() {
        @JvmStatic
        private val dynmodb = DynamoDb

        @JvmStatic
        protected val snowflake = GlobalSnowflake()
    }
}
