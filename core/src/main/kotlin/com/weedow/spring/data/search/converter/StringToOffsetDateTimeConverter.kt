package com.weedow.spring.data.search.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@ReadingConverter
class StringToOffsetDateTimeConverter(
        private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
) : Converter<String, OffsetDateTime> {

    override fun convert(source: String): OffsetDateTime {
        return OffsetDateTime.parse(source, dateTimeFormatter)
    }

}