package io.bluetape4k.lang

import org.apache.commons.lang3.LocaleUtils
import java.util.*

fun availableLocaleList(): List<Locale> = LocaleUtils.availableLocaleList()

fun availableLocaleList(predicate: (Locale) -> Boolean): List<Locale> =
    availableLocaleList().filter(predicate)

fun availableLocaleSet(): Set<Locale> = LocaleUtils.availableLocaleSet()

fun availableLocaleSet(predicate: (Locale) -> Boolean): Set<Locale> =
    LocaleUtils.availableLocaleSet().filter(predicate).toSet()

fun countriesByLanguage(language: String): List<Locale> =
    LocaleUtils.countriesByLanguage(language)

fun Locale.isAvailable(): Boolean = LocaleUtils.isAvailableLocale(this)

fun Locale.isLanguageUndetermined(): Boolean = LocaleUtils.isLanguageUndetermined(this)

fun languageByCountry(countryCode: String): List<Locale> = LocaleUtils.languagesByCountry(countryCode)

/**
 * Obtains the list of locales to search through when performing
 * a locale search.
 *
 * ```
 * localeLookupList(Locale("fr", "CA", "xxx"))
 *   = [Locale("fr", "CA", "xxx"), Locale("fr", "CA"), Locale("fr")]
 * ```
 *
 * @receiver the locale to start from
 * @return the unmodifiable list of Locale objects, 0 being locale, not null
 */
fun Locale.localeLookupList(defaultLocale: Locale = this): List<Locale> =
    LocaleUtils.localeLookupList(this, defaultLocale)

fun localeOf(str: String): Locale = LocaleUtils.toLocale(str)
