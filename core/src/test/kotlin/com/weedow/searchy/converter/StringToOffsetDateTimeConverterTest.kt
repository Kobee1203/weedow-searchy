package com.weedow.searchy.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal class StringToOffsetDateTimeConverterTest {

    @Test
    fun convert_string_to_offset_date_time() {
        val converter = StringToOffsetDateTimeConverter()

        val offsetDateTime1 = OffsetDateTime.of(1981, 3, 12, 10, 36, 25, 0, ZoneOffset.ofHours(-7))
        assertThat(converter.convert("1981-03-12T10:36:25-07:00")).isEqualTo(offsetDateTime1)

        val offsetDateTime2 = OffsetDateTime.of(1981, 3, 12, 10, 36, 25, 235000000, ZoneOffset.ofHours(-7))
        assertThat(converter.convert("1981-03-12T10:36:25.235-07:00")).isEqualTo(offsetDateTime2)
    }

    @Test
    fun convert_string_to_offset_date_time_with_custom_pattern() {
        val converter = StringToOffsetDateTimeConverter(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][Z]"))

        val offsetDateTime1 = OffsetDateTime.of(1981, 3, 12, 10, 36, 25, 0, ZoneOffset.ofHours(-7))
        assertThat(converter.convert("1981-03-12T10:36:25-0700")).isEqualTo(offsetDateTime1)
        val offsetDateTime2 = OffsetDateTime.of(1981, 3, 12, 10, 36, 25, 235000000, ZoneOffset.ofHours(-7))
        assertThat(converter.convert("1981-03-12T10:36:25.235-0700")).isEqualTo(offsetDateTime2)
    }

}