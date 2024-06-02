package io.bluetape4k.workshop.elasticsearch

import io.bluetape4k.logging.KLogging
import org.springframework.boot.test.context.SpringBootTest
import java.text.SimpleDateFormat

@SpringBootTest
abstract class AbstractElasticsearchTest {

    companion object: KLogging() {
        val format = SimpleDateFormat("yyyy-MM-dd")
    }

}
