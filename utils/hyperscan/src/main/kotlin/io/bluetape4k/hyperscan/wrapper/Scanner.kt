package io.bluetape4k.hyperscan.wrapper

import com.gliwka.hyperscan.jni.hyperscan.hs_alloc_scratch
import com.gliwka.hyperscan.jni.hyperscan.hs_scan
import com.gliwka.hyperscan.jni.hyperscan.hs_scratch_size
import com.gliwka.hyperscan.jni.hyperscan.hs_valid_platform
import com.gliwka.hyperscan.jni.hyperscan.hs_version
import com.gliwka.hyperscan.jni.match_event_handler
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.support.toUtf8Bytes
import kotlinx.atomicfu.atomic
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.Pointer
import org.bytedeco.javacpp.SizeTPointer
import java.io.Closeable
import java.nio.ByteBuffer
import java.util.*

fun scannerOf(db: Database): Scanner = Scanner().apply { allocScratch(db) }

/**
 * Scanner, can be used with databases to scan for expressions in input string
 * In case of multithreaded scanning, you need one scanner instance per CPU thread.
 *
 * Scanner references native resources. It is paramount to close it after use.
 * There can only be 256 non-closed scanner instances.
 */
class Scanner: Closeable {

    companion object: KLogging() {
        const val MAX_SCANNER_COUNT = 256
        private val counter = atomic(0)

        /**
         * Check if the platform is valid for use with hyperscan
         */
        val isValidPlatform: Boolean = hs_valid_platform() == 0

        /**
         * Get the version information for the underlying hyperscan library
         */
        val version: String = hs_version().string
    }

    init {
        // The function pointer for the callback match_event_handler allocates native resources.
        // javacpp limits the number of function pointer instances to 10.
        // The limit has been increased to 256 to match the thread count in modern server CPUs
        // An alternative would be to have a single callback and to use the context pointer to identify
        // the right scanner. I've decided against it to keep this implementation simple and to not have
        // to manage references between context pointers and scanner instances

        if (counter.incrementAndGet() > MAX_SCANNER_COUNT) {
            error("현재 생성되어 활성화된 Scanner가 $MAX_SCANNER_COUNT 를 넘었습니다. Scanner는 Thread 당 하나만 생성하세요.")
        }
    }

    private var scratch: NativeScratch? = NativeScratch()

    /**
     * Get the scratch space size in bytes
     *
     * @return count of bytes
     */
    fun getSize(): Long {
        assertScratchAllocated()

        SizeTPointer(1).use { size ->
            hs_scratch_size(scratch, size)
            return size.get()
        }
    }

    /**
     * Allocate a scratch space.  Must be called at least once with each
     * database that will be used before scan is called.
     * @param db Database containing expressions to use for matching
     */
    fun allocScratch(db: Database) {
        assertScratchAllocated()

        val dbPointer = db.getNativeDatabase()
        val hsError = hs_alloc_scratch(dbPointer, scratch)
        scratch!!.registerDeallocator()

        if (hsError != 0) {
            throw hyperscanExceptionOf(hsError)
        }
    }

    private val matchedIds = LinkedList<LongArray>()

    private val matchHandler: match_event_handler = object: match_event_handler() {
        override fun call(id: Int, from: Long, to: Long, flags: Int, context: Pointer?): Int {
            val tuple = longArrayOf(id.toLong(), from, to)
            matchedIds.add(tuple)
            return 0
        }
    }

    /**
     * scan for a match in a string using a compiled expression database
     * Can only be executed one at a time on a per instance basis
     * @param db Database containing expressions to use for matching
     * @param input String to match against
     * @return List of Matches
     */
    fun scan(db: Database, input: String): List<Match> {
        assertScratchAllocated()

        val database = db.getNativeDatabase()

        log.trace { "Scanning input=$input, database=$database" }

        matchedIds.clear()
        val bytes = input.toUtf8Bytes()
        BytePointer(ByteBuffer.wrap(bytes)).use { bytePointer ->
            val hsError = hs_scan(database, bytePointer, bytes.size, 0, scratch, matchHandler, null)
            log.trace { "hs_scan returned $hsError" }
            if (hsError != 0) {
                throw hyperscanExceptionOf(hsError)
            }
        }
        if (matchedIds.isEmpty()) {
            return emptyList()
        }

        if (bytes.size == input.length) {
            return processMatches(input, bytes, db) { it }.toList()
        } else {
            val byteToStringPositin = Utf8.byteToStringPositiveMap(input, bytes.size)
            return processMatches(input, bytes, db) { byteToStringPositin[it] }.toList()
        }
    }

    fun processMatches(
        input: String,
        bytes: ByteArray,
        db: Database,
        position: (Int) -> Int,
    ): Sequence<Match> = sequence {
        matchedIds.forEach { tuple ->
            val id = tuple[0].toInt()
            val from = tuple[1]
            val to = maxOf(1, tuple[2])
            val matchingExpression = db.getExpression(id)!!

            val startIndex = position(from.toInt())
            val endIndex = position(to.toInt() - 1)

            val matchedString = if (matchingExpression.flags.contains(ExpressionFlag.SOM_LEFTMOST)) {
                input.substring(startIndex, endIndex + 1)
            } else ""

            val match = if (bytes.isNotEmpty()) {
                Match(startIndex, endIndex, matchedString, matchingExpression)
            } else {
                Match(0, 0, matchedString, matchingExpression)
            }
            yield(match)
        }
    }

    private fun assertScratchAllocated() {
        if (scratch == null) {
            error("Scratch space has already been deallocated")
        }
    }


    override fun close() {
        scratch?.close()
        matchHandler.close()
        counter.decrementAndGet()
        scratch = null
    }
}
