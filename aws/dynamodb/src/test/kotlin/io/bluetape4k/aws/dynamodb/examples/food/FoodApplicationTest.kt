package io.bluetape4k.aws.dynamodb.examples.food

import io.bluetape4k.aws.dynamodb.AbstractDynamodbTest
import io.bluetape4k.aws.dynamodb.examples.food.repository.FoodRepository
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FoodApplicationTest: AbstractDynamodbTest() {

    companion object: KLogging() {
        // Localstack 을 실행하기 위해 참조한다
        private val expectedEndpoint = endpoint
    }

    @Autowired
    private lateinit var repository: FoodRepository

    @Value("\${aws.dynamodb.endpoint}")
    private lateinit var dynamoDbEndpoint: String

    @Test
    fun `context loading`() {
        repository.shouldNotBeNull()

        log.info { "DynamoDb endpoint=$dynamoDbEndpoint" }
        dynamoDbEndpoint shouldBeEqualTo "http://localhost:${DynamoDb.port}"
    }
}
