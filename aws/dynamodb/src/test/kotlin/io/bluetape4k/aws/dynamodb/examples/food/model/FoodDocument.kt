package io.bluetape4k.aws.dynamodb.examples.food.model

import io.bluetape4k.aws.dynamodb.model.makeKey
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.support.requireNotBlank
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import java.time.Instant

@DynamoDbBean
class FoodDocument: AbstractDynamoDocument() {

    companion object {

        @JvmStatic
        operator fun invoke(
            id: String,
            restraurantId: String,
            state: FoodState = FoodState.UNKOWN,
            updatedAt: Instant? = null,
        ): FoodDocument {
            id.requireNotBlank("id")
            restraurantId.requireNotBlank("restruantId")

            return FoodDocument().apply {
                this.id = id
                this.restraurantId = restraurantId
                this.state = state
                this.updatedAt = updatedAt

                this.partitionKey = makeKey(restraurantId)
                this.sortKey = makeKey(restraurantId, id)
            }
        }
    }

    var id: String = ""
    var restraurantId: String = ""
    var state: FoodState = FoodState.UNKOWN

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("id", id)
            .add("restraurantId", restraurantId)
            .add("state", state)
            .add("updatedAt", updatedAt)
    }
}
