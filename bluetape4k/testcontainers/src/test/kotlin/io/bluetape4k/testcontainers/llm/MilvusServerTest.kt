package io.bluetape4k.testcontainers.llm

import io.bluetape4k.logging.KLogging
import io.milvus.grpc.DataType
import io.milvus.param.collection.CollectionSchemaParam
import io.milvus.param.collection.CreateCollectionParam
import io.milvus.param.collection.FieldType
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

// FIXME: Milvus server is not working
@Disabled("Milvus server is not working")
@Execution(ExecutionMode.SAME_THREAD)
class MilvusServerTest {

    companion object: KLogging()

    @Test
    fun `create milvus server`() {
        MilvusServer().use { milvus ->
            milvus.start()
            milvus.isRunning.shouldBeTrue()

            verifyMilvusServer(milvus)
        }
    }

    private fun verifyMilvusServer(milvus: MilvusServer) {
        val client = milvus.getClient()

        val fieldType1: FieldType = FieldType.newBuilder()
            .withName("book_id")
            .withDataType(DataType.Int64)
            .withPrimaryKey(true)
            .withAutoID(false)
            .build()
        val fieldType2: FieldType = FieldType.newBuilder()
            .withName("word_count")
            .withDataType(DataType.Int64)
            .build()
        val fieldType3: FieldType = FieldType.newBuilder()
            .withName("book_intro")
            .withDataType(DataType.FloatVector)
            .withDimension(2)
            .build()
        val createCollectionReq: CreateCollectionParam = CreateCollectionParam.newBuilder()
            .withCollectionName("book")
            .withDescription("Test book search")
            .withShardsNum(2)
            //            .addFieldType(fieldType1)
            //            .addFieldType(fieldType2)
            //            .addFieldType(fieldType3)
            .withSchema(
                CollectionSchemaParam.newBuilder()
                    .addFieldType(fieldType1)
                    .addFieldType(fieldType2)
                    .addFieldType(fieldType3)
                    .build()
            )
            .build()

        client.createCollection(createCollectionReq)

        client.close()
    }
}
