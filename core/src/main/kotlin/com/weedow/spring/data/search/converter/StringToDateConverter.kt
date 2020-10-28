package com.weedow.spring.data.search.converter

import com.weedow.spring.data.search.utils.klogger
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.util.*

/**
 * A converter that converts from String to [Date].
 *
 * It uses the default JVM [Locale] and a default list of patterns defined by [DATE_FORMATS][com.weedow.spring.data.search.converter.StringToDateConverter.Companion.DATE_FORMATS].
 *
 * It is possible to modify the default behavior by instantiating the converter with a specific [Locale] and/or a specific patterns list.
 */
@ReadingConverter
class StringToDateConverter(
        vararg val patterns: String = DATE_FORMATS,
        val locale: Locale = Locale.getDefault(Locale.Category.FORMAT)
) : Converter<String, Date> {

    companion object {
        private val DATE_FORMATS = arrayOf(
                "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX][Z]",
                "yyyy-MM-dd",
                "yyyyMMdd",
                "dd/MM/yyyy"
        )

        private val log by klogger()
    }

    private val dateTimeFormatter: DateTimeFormatter

    init {
        val joinedPatterns = patterns.joinToString("][", "[", "]")
        if (log.isDebugEnabled) log.debug("initializing DateTimeFormatter with the locale $locale and the following patterns: $joinedPatterns")

        val dateTimeFormatterBuilder = DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .parseLenient()
                .append(DateTimeFormatter.ofPattern(joinedPatterns, locale))

        dateTimeFormatter = dateTimeFormatterBuilder.toFormatter(locale)
    }

    override fun convert(source: String): Date {
        val localDateTime = try {
            val offsetDateTime = OffsetDateTime.parse(source, dateTimeFormatter)
            offsetDateTime.toLocalDateTime()
        } catch (e: DateTimeParseException) {
            try {
                LocalDateTime.parse(source, dateTimeFormatter)
            } catch (e: DateTimeParseException) {
                val localDate = LocalDate.parse(source, dateTimeFormatter)
                localDate.atStartOfDay()
            }
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

}