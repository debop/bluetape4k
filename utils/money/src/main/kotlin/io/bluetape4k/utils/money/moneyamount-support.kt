@file:Suppress("UNCHECKED_CAST")

package io.bluetape4k.utils.money

import java.math.BigDecimal
import java.math.BigInteger
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.MonetaryAmount
import javax.money.MonetaryRounding
import javax.money.convert.MonetaryConversions


/**
 * 통화량을 나타내는 [MonetaryAmount]를 빌드합니다.
 *
 * @param number        통화량
 * @param currencyCode  통화단위 코드 ("KRW", "USD", "EUR")
 * @return 통화량([MonetaryAmount]) instance
 */
fun monetaryAmountOf(number: Number, currencyCode: String): MonetaryAmount =
    monetaryAmountOf(number, currencyUnitOf(currencyCode))

/**
 * 통화량을 나타내는 [MonetaryAmount]를 빌드합니다.
 *
 * @param number        통화량
 * @param currency      통화단위 ([CurrencyUnit])
 * @return 통화량([MonetaryAmount]) instance
 */
fun monetaryAmountOf(number: Number, currency: CurrencyUnit = DefaultCurrencyUnit): MonetaryAmount =
    Monetary.getDefaultAmountFactory().setCurrency(currency).setNumber(number).create()

/**
 * 통화량을 나타내는 [MonetaryAmount]를 빌드합니다.
 *
 * @param currency      통화단위 ([CurrencyUnit])
 * @return 통화량([MonetaryAmount]) instance
 */
fun Number.toMonetaryAmount(currency: CurrencyUnit = DefaultCurrencyUnit): MonetaryAmount =
    Monetary.getDefaultAmountFactory().setCurrency(currency).setNumber(this).create()

/**
 * 통화량을 나타내는 [MonetaryAmount]를 빌드합니다.
 *
 * @param currencyCode  통화단위 코드 ("KRW", "USD", "EUR")
 * @return 통화량([MonetaryAmount]) instance
 */
fun Number.toMonetaryAmount(currencyCode: String): MonetaryAmount =
    monetaryAmountOf(this, currencyCode)

//
// MonetaryAmount Arithmetic Operators
//
operator fun <T: MonetaryAmount> T.unaryMinus(): T = this.negate() as T

operator fun <T: MonetaryAmount> T.plus(other: MonetaryAmount): T = this.add(other) as T
operator fun <T: MonetaryAmount> T.plus(scalar: Number): T = this.add(scalar.toMonetaryAmount(currency)) as T
operator fun <T: MonetaryAmount> T.minus(other: MonetaryAmount): T = this.subtract(other) as T
operator fun <T: MonetaryAmount> T.minus(scalar: Number): T = this.subtract(scalar.toMonetaryAmount(currency)) as T

operator fun <T: MonetaryAmount> T.times(scalar: Number): T = this.multiply(scalar) as T
operator fun <T: MonetaryAmount> Number.times(amount: T): T = amount.multiply(this) as T

operator fun <T: MonetaryAmount> T.div(scalar: Number): T = this.divide(scalar) as T

/**
 * [MonetaryAmount]에서 금액을 원하는 수형으로 가져온다
 */
inline fun <reified T: Number> MonetaryAmount.numberValue(): T = number.numberValue(T::class.java)

val MonetaryAmount.intValue: Int get() = numberValue()
val MonetaryAmount.longValue: Long get() = numberValue()
val MonetaryAmount.floatValue: Float get() = numberValue()
val MonetaryAmount.doubleValue: Double get() = numberValue()
val MonetaryAmount.bigDecimalValue: BigDecimal get() = numberValue()
val MonetaryAmount.bigIntValue: BigInteger get() = numberValue()

/**
 * [MonetaryAmount] 수형의 금액을 반올림합니다.
 */
fun <T: MonetaryAmount> T.round(rounding: MonetaryRounding = Monetary.getRounding(this.currency)): T =
    this.with(rounding) as T

/**
 * 기본 반올림 규칙에 의해 [MonetaryAmount] 수형의 금액을 반올림합니다.
 */
fun <T: MonetaryAmount> T.defaultRound(): T = this.with(Monetary.getDefaultRounding()) as T

/**
 * EU의 오늘자 환율 정보를 이용하여 [receiver]의 금액을 대상 금액으로 환전합니다
 *
 * ```
 * 1.05L.inUSD().convertTo("KRW")        // USD 1.05 를 원화로 환젼
 * ```
 *
 * @param T
 * @param currencyCode 환전할 통화 코드 ("USD", "KRW", "EUR")
 * @return
 */
fun <T: MonetaryAmount> T.convertTo(currencyCode: String): T =
    this.with(MonetaryConversions.getConversion(currencyCode)) as T

/**
 * EU의 오늘자 환율 정보를 이용하여 [receiver]의 금액을 대상 금액으로 환전합니다
 *
 * ```
 * 1.05L.inUSD().convertTo(currencyOf("KRW"))        // USD 1.05 를 원화로 환젼
 * ```
 *
 * @param T 통화량 수형
 * @param currencyCode 환전할 통화 단위
 * @return 환전한 통화량
 */
fun <T: MonetaryAmount> T.convertTo(currency: CurrencyUnit = DefaultCurrencyUnit): T =
    this.with(MonetaryConversions.getConversion(currency)) as T

/**
 * 금액을 합산합니다
 * @param currency 환전할 통화 단위
 * @return 환전한 통화량([MonetaryAmount])
 */
fun Collection<MonetaryAmount>.sum(currency: CurrencyUnit = DefaultCurrencyUnit): MonetaryAmount {
    if (isEmpty()) {
        return 0L.toMonetaryAmount(currency)
    }

    var sum = 0L.toMonetaryAmount(currency)
    forEach {
        sum += it.convertTo(currency)
    }
    return sum
}
