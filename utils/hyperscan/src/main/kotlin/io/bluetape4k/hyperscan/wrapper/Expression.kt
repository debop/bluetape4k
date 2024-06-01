package io.bluetape4k.hyperscan.wrapper

import com.gliwka.hyperscan.jni.hs_compile_error_t
import com.gliwka.hyperscan.jni.hs_expr_info_t
import com.gliwka.hyperscan.jni.hyperscan.hs_expression_info
import io.bluetape4k.support.requireNotBlank
import java.io.Serializable
import java.util.*

/**
 * Expression to be compiled as a Database and then be used for scanning using the Scanner
 */
data class Expression(
    val expression: String,
    val flags: EnumSet<ExpressionFlag> = EnumSet.of(ExpressionFlag.NO_FLAG),
    val id: Int? = null,
): Serializable {

    companion object {
        @JvmStatic
        @JvmOverloads
        operator fun invoke(
            expression: String,
            flag: ExpressionFlag = ExpressionFlag.NO_FLAG,
            id: Int? = null,
        ): Expression {
            return Expression(expression, EnumSet.of(flag), id)
        }

        @JvmStatic
        operator fun invoke(
            expression: String,
            id: Int?,
            flag: ExpressionFlag,
            vararg flags: ExpressionFlag,
        ): Expression {
            return Expression(expression, EnumSet.of(flag, *flags), id)
        }
    }

    init {
        expression.requireNotBlank("expression")

        if (id != null && id < 0) {
            throw IllegalArgumentException("Expression id[$id] must be zero or positive")
        }
    }

    val flagBits: Int
        get() = flags.fold(0) { acc, flag -> acc or flag.bits }

    fun validate(): ValidationResult {
        hs_expr_info_t().use { info ->
            val error = hs_compile_error_t()
            val hsResult = hs_expression_info(expression, flagBits, info, error)

            return when (hsResult) {
                0    -> ValidationResult(true)
                else -> ValidationResult(false, error.message().string)
            }
        }
    }

    data class ValidationResult(
        val isValid: Boolean,
        val message: String? = null,
    ): Serializable
}

/**
 * compile an expression into a database to use for scanning
 *
 * @receiver Expression to compile
 * @return Compiled database
 */
fun Expression.compile(): Database {
    return compileDatabaseOf(this)
}

/**
 * compile an expressions into a database to use for scanning
 *
 * @receiver Expressions to compile
 * @return Compiled database
 */
fun List<Expression>.compile(): Database {
    return compileDatabaseOf(this)
}
