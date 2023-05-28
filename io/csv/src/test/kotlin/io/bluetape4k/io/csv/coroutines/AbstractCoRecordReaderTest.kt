package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.common.record.Record
import io.bluetape4k.io.csv.model.ProductType
import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.buffer
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldNotBeBlank
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.UTF_8

abstract class AbstractCoRecordReaderTest {

    companion object: KLogging()

    protected abstract val reader: CoRecordReader

    protected abstract val productTypePath: String
    protected abstract val extraWordsPath: String

    private val mapper: (Record) -> ProductType = { record: Record ->
        val tagFamily = record.getValue(0, "").trim()
        val representative = record.getValue(1, "").trim()
        val synonym = record.getValue<String?>(2, null)?.trim()
        val tagType = record.getValue<String?>(3, null)?.trim()
        val priority = record.getValue<Int?>(4, null)
        val parentRepresentative = record.getValue<String?>(5, null)?.trim()
        val level = record.getValue(6, 0)

        ProductType(
            tagFamily,
            representative,
            synonym,
            tagType,
            priority,
            parentRepresentative,
            level
        )
    }

    @Test
    fun `read record from csv file with number types`() = runSuspendWithIO {
        Resourcex.getInputStream(productTypePath)!!.buffered().use { input ->
            reader
                .read(input, UTF_8, true)
                .buffer()
                .collect { record ->
                    log.trace { "product type record=$record" }
                    val row = record.values.toList()
                    row.shouldNotBeEmpty()
                    row.size shouldBeGreaterThan 1
                    row[0]!!.shouldNotBeBlank()
                    row[1]!!.shouldNotBeBlank()
                }
        }
        coroutineContext.cancelChildren()
    }

    @Test
    fun `read product type from csv file with mapper`() = runSuspendWithIO {
        Resourcex.getInputStream(productTypePath)!!.buffered().use { input ->
            reader
                .read(input, UTF_8, true, mapper)
                .buffer()
                .collect { productType ->
                    log.trace { "ProductType=$productType" }
                    productType.shouldNotBeNull()
                    productType.tagFamily.shouldNotBeBlank()
                    productType.representative.shouldNotBeBlank()
                }
        }
        coroutineContext.cancelChildren()
    }

    @Test
    fun `read extra words from csv file `() = runSuspendWithIO {
        Resourcex.getInputStream(extraWordsPath)!!.buffered().use { input ->

            reader
                .read(input, UTF_8, true)
                .buffer()
                .collect { record ->
                    log.trace { "extra words record=$record" }
                    val row = record.values
                    row.shouldNotBeEmpty()
                    row.size shouldBeGreaterThan 1
                    row[0]!!.shouldNotBeBlank()
                    row[4]!!.shouldNotBeBlank()
                }
        }
        coroutineContext.cancelChildren()
    }
}
