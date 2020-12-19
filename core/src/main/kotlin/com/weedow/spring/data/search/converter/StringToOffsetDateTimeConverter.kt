package com.weedow.spring.data.search.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * A converter that converts from String to [OffsetDateTime].
 *
 * It uses [DateTimeFormatter.ISO_OFFSET_DATE_TIME] by default.
 *
 * It is possible to modify the default behavior by instantiating the converter with a specific [DateTimeFormatter].
 *
 * @param dateTimeFormatter the formatter to be used while converting. Default is [DateTimeFormatter.ISO_OFFSET_DATE_TIME]
 */
@ReadingConverter
class StringToOffsetDateTimeConverter(
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
) : Converter<String, OffsetDateTime> {

    /**
     * Converts the given [String] to [OffsetDateTime].
     *
     * @param source String to be converted
     * @return the OffsetDateTime instance
     */
    override fun convert(source: String): OffsetDateTime {
        return OffsetDateTime.parse(source, dateTimeFormatter)
    }

}