package io.bluetape4k.hyperscan.wrapper

import com.gliwka.hyperscan.jni.hs_compile_error_t
import com.gliwka.hyperscan.jni.hyperscan
import com.gliwka.hyperscan.jni.hyperscan.hs_database_size
import com.gliwka.hyperscan.jni.hyperscan.hs_serialize_database
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.support.hashOf
import org.bytedeco.javacpp.BytePointer
import org.bytedeco.javacpp.IntPointer
import org.bytedeco.javacpp.PointerPointer
import org.bytedeco.javacpp.SizeTPointer
import java.io.Closeable
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.Channels
import java.util.*

/**
 * Database containing compiled expressions ready for scanning using the Scanner
 */
class Database private constructor(
    private var database: NativeDatabase?,
    private var expressions: MutableMap<Int, Expression> = hashMapOf(),
): Closeable {

    companion object: KLogging() {

        @JvmStatic
        internal operator fun invoke(database: NativeDatabase, expressions: List<Expression>): Database {
            val expressionMap = HashMap<Int, Expression>(expressions.size)
            val hasIds = expressions.first().id != null

            if (hasIds) {
                expressions.forEach { expression ->
                    if (expressionMap.put(expression.id!!, expression) != null) {
                        error("Expression id ${expression.id} is not unique")
                    }
                }
            } else {
                expressions.forEachIndexed { index, expression ->
                    expressionMap[index] = expression
                }
            }

            return Database(database, expressionMap)
        }
    }

    private var expressionCount: Int = expressions.size

    fun getSize(): Long {
        assertNotClosed()

        return SizeTPointer(1).use { size ->
            hs_database_size(database, size)
            size.get()
        }
    }

    fun getNativeDatabase(): NativeDatabase? = database

    fun getExpression(id: Int): Expression? = expressions[id]

    private fun assertNotClosed() {
        if (database == null) {
            error("Database has already been deallocated")
        }
    }

    override fun close() {
        database?.close()
        database = null
    }

    /**
     * Saves the expressions and the compiled database to an OutputStream.
     * Expression contexts are not saved.
     * The OutputStream is not closed.
     *
     * @param out stream to write to
     */
    fun save(out: OutputStream) {
        save(out, out)
    }

    /**
     * Saves the expressions and the compiled database to (possibly) distinct OutputStreams.
     * All of the expressions are saved to expressionsOut before any of the database is saved to databaseOut so it's safe
     * to use the same backing OutputStream for both parameters.
     * Expression contexts are not saved.
     * Neither of the OutputStream is closed.
     *
     * @param expressionsOut stream to write expressions to
     * @param databaseOut    stream to write database to
     */
    fun save(expressionsOut: OutputStream, databaseOut: OutputStream) {
        val expressionsDataOut = DataOutputStream(expressionsOut)
        log.trace { "Save expression count=$expressionCount" }
        expressionsDataOut.writeInt(expressionCount)
        expressions.values
            //.filterNotNull()
            .forEach { expression ->
                log.trace { "Save expression. id=${expression.id ?: -1}, expression=${expression.expression}, flags=${expression.flags}" }
                // Expression Id
                expressionsDataOut.writeInt(expression.id ?: -1)
                // Expression pattern
                expressionsDataOut.writeUTF(expression.expression)

                // Flag count
                expressionsDataOut.writeInt(expression.flags.size)

                expression.flags.forEach { flag ->
                    // Bitmask for each flag
                    log.trace { "Save flag. flag=$flag, bits=${flag.bits}" }
                    expressionsDataOut.writeInt(flag.bits)
                }
            }
        expressionsDataOut.flush()

        // Serialize the database into a contiguous native memory block
        val bytePointer = BytePointer(1L)
        val size = SizeTPointer(1L)

        try {
            val hsError = hs_serialize_database(database, bytePointer, size)
            if (hsError != 0) {
                throw hyperscanExceptionOf(hsError)
            }
            val length = size.get()

            // Write the native memory to the output stream.
            // We could just load all the native memory onto the heap but that would double our memory usage.
            // Instead we copy small blocks at a time
            val buffer = bytePointer.capacity(length).asBuffer()

            val databaseDataOut = DataOutputStream(databaseOut)
            databaseDataOut.writeInt(length.toInt())

            // Neither DataOutputStream nor WritableByteChannel buffer so we can intermix usage.
            Channels.newChannel(databaseDataOut).write(buffer)
            databaseDataOut.flush()
        } finally {
            bytePointer.close()
            size.close()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Database &&
                expressionCount == other.expressionCount &&
                expressions == other.expressions
    }

    override fun hashCode(): Int {
        return hashOf(expressionCount, expressions)
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .add("expressionCount", expressionCount)
            .add("expressions", expressions)
            .toString()
    }
}


/**
 * compile an expression into a database to use for scanning
 *
 * @param expression Expression to compile
 * @return Compiled database
 */
@Throws(CompileErrorException::class)
fun compileDatabaseOf(expression: Expression): Database {
    return compileDatabaseOf(listOf(expression))
}

/**
 * Compiles an list of expressions into a database to use for scanning
 *
 * @param expressions List of expressions to compile
 * @return Compiled database
 */
@Throws(CompileErrorException::class)
fun compileDatabaseOf(expressions: List<Expression>): Database {
    val expressionsSize = expressions.size
    val expressionArray = expressions.map { it.expression }.toTypedArray()
    val nativeExpressions = PointerPointer<BytePointer>(expressionsSize.toLong()).apply {
        putString(expressionArray, Charsets.UTF_8)
    }

    val flags = IntArray(expressionsSize)
    val ids = IntArray(expressionsSize)

    val expressionWithoutId = expressions.any { it.id == null }
    val expressionWithId = expressions.any { it.id != null }

    if (expressionWithId && expressionWithoutId) {
        error("You can't mix expressions with and without id's in a single database")
    }

    expressions.forEachIndexed { index, expression ->
        flags[index] = expression.flagBits
        ids[index] = if (expressionWithId) expression.id!! else index
    }


    IntPointer(*flags).use { nativeFlags ->
        IntPointer(*ids).use { nativeIds ->
            PointerPointer<NativeDatabase>(1).use { database ->
                PointerPointer<hs_compile_error_t>(hs_compile_error_t()).use { error ->
                    val hsError = hyperscan.hs_compile_multi(
                        nativeExpressions,
                        nativeFlags,
                        nativeIds,
                        expressionsSize,
                        hyperscan.HS_MODE_BLOCK,
                        null,
                        database,
                        error,
                    )
                    handleErrors(hsError, error.get(hs_compile_error_t::class.java), expressions)

                    return Database(database.get(NativeDatabase::class.java), expressions)
                }
            }
        }
    }
}

/**
 * Loads the database saved via {@link #save(OutputStream)}.
 * The saved payload contains platform-specific formatting so it should be loaded on a compatible platform.
 * All Expression contexts will be null.
 *
 * @param input stream to read from
 * @return loaded Database
 */
fun loadDatabase(input: InputStream): Database {
    return loadDatabase(input, input)
}

// Setup a lookup map for epxression flags
@JvmField
internal val BitmaskToFlag = ExpressionFlag.entries.associateBy { it.bits }

/**
 * Loads the database saved via {@link #save(OutputStream, OutputStream)}.
 * The saved payload contains platform-specific formatting so it should be loaded on a compatible platform.
 *
 * @param expressionsIn stream to read expressions from
 * @param databaseIn    stream to read database from
 * @return loaded Database
 */
fun loadDatabase(expressionsIn: InputStream, databaseIn: InputStream): Database {
    // DataInputStream doesn't buffer so it will only read as much as we ask for.
    // This makes it safe to use even if expressionsIn and databaseIn are the same streams.
    val expressionsDataIn = DataInputStream(expressionsIn)
    val expressionCount = expressionsDataIn.readInt()
    val expressions = arrayListOf<Expression>()

    repeat(expressionCount) {
        val id = expressionsDataIn.readInt()
        val pattern = expressionsDataIn.readUTF()
        val flagCount = expressionsDataIn.readInt()
        val flags = EnumSet.noneOf(ExpressionFlag::class.java)
        repeat(flagCount) {
            val bitmask = expressionsDataIn.readInt()
            flags.add(BitmaskToFlag[bitmask])
        }
        expressions.add(Expression(pattern, flags, if (id == -1) null else id))
    }

    val databaseDataIn = DataInputStream(databaseIn)
    val length = databaseDataIn.readInt()
    val bytes = ByteArray(length)
    val bytePointer = BytePointer(length.toLong())
    databaseDataIn.readFully(bytes)
    bytePointer.put(*bytes)

    val database = NativeDatabase()
    val hsError = hyperscan.hs_deserialize_database(bytePointer, length.toLong(), database)
    if (hsError != 0) {
        throw hyperscanExceptionOf(hsError)
    }

    return Database(database, expressions)
}


@Throws(CompileErrorException::class)
private fun handleErrors(hsError: Int, compileError: hs_compile_error_t?, expressions: List<Expression>) {
    if (hsError == 0) return

    if (hsError == hyperscan.HS_COMPILER_ERROR) {
        compileError!!
        val expression = if (compileError.expression() < 0) null else expressions[compileError.expression()]
        throw CompileErrorException(compileError.message().string, expression)
    } else {
        throw hyperscanExceptionOf(hsError)
    }
}
