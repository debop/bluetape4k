package io.bluetape4k.utils.idgenerators

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.idgenerators.snowflake.MAX_MACHINE_ID
import java.net.NetworkInterface
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInRange
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue

class IdSupportTest {

    companion object : KLogging()

    private val networkId = buildString {
        NetworkInterface.getNetworkInterfaces().asSequence()
            .forEach { network ->
                network.hardwareAddress?.run {
                    forEach { elem -> append("%02X".format(elem)) }
                }
            }
    }.hashCode().absoluteValue % MAX_MACHINE_ID

    @Test
    fun `create machine id by network address`() {
        log.debug { "networkId=$networkId" }

        val machineId = getMachineId(MAX_MACHINE_ID)

        machineId shouldBeInRange (0 until MAX_MACHINE_ID)
        machineId shouldBeEqualTo networkId
    }
}
