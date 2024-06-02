package io.bluetape4k.workshop.redis.examples.stream

import io.bluetape4k.logging.KLogging
import org.springframework.data.redis.connection.stream.RecordId
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.connection.stream.StringRecord

/**
 * IoT 기기로부터 받은 Sensor data
 *
 * @see [org.springframework.data.redis.connection.stream.RecordId]
 * @see [org.springframework.data.redis.connection.stream.StringRecord]
 * @see [org.springframework.data.redis.connection.stream.MapRecord]
 */
class SensorData {

    companion object: KLogging() {
        const val KEY = "my-stream"

        // NOTE: 참고: RecordId 는 `epoch milliseconds-sequence id` 형식으로 구성됩니다. [RecordId] 참고 
        // 1234 는 Timestamp, 0, 1 은 sequence 를 의미합니다.
        // 
        val RECORD_1234_0: StringRecord = create("5-12", "18", "r2d2").withId(RecordId.of("1234-0"))
        val RECORD_1234_1: StringRecord = create("5-13", "9", "c3o0").withId(RecordId.of("1234-1"))
        val RECORD_1235_0: StringRecord = create("5-13", "18.2", "bb8").withId(RecordId.of("1235-0"))

        /**
         * Spring Data Redis에서 제공하는 [StreamRecords.string] 메소드를 이용하여 [StringRecord]를 생성합니다.
         *
         * @param sensor       센서 ID
         * @param temperature  측정 온도
         * @param checksum     Checksum
         * @return [StringRecord] 인스턴스 (StreamKey는 [KEY]로 고정)
         */
        @JvmStatic
        fun create(sensor: String, temperature: String, checksum: String): StringRecord {
            return StreamRecords.string(sensorData(sensor, temperature, checksum)).withStreamKey(KEY)
        }

        private fun sensorData(vararg values: String): Map<String, String> {
            return mutableMapOf(
                "sensor-id" to values[0],
                "temperature" to values[1],
            ).apply {
                if (values.size >= 3) {
                    put("checksum", values[2])
                }
            }
        }
    }
}
