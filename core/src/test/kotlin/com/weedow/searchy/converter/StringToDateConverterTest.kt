package com.weedow.searchy.converter

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

internal class StringToDateConverterTest {

    @Test
    fun convert_string_to_date() {
        val converter = StringToDateConverter()

        // 1981-03-12 00:00:00
        val expected = createDate(1981, Calendar.MARCH, 12, 0, 0, 0, 0)

        assertThat(converter.convert("19810312")).isEqualTo(expected)
        assertThat(converter.convert("1981-03-12")).isEqualTo(expected)
        assertThat(converter.convert("12/03/1981")).isEqualTo(expected)
    }

    @Test
    fun convert_string_to_date_with_time() {
        val converter = StringToDateConverter()

        // 1981-03-12 10:36:25
        val expected = createDate(1981, Calendar.MARCH, 12, 10, 36, 25, 0)
        assertThat(converter.convert("1981-03-12T10:36:25")).isEqualTo(expected)

        // 1981-03-12 10:36:25 235ms
        val expectedWithMilliseconds = createDate(1981, Calendar.MARCH, 12, 10, 36, 25, 235)
        assertThat(converter.convert("1981-03-12T10:36:25.235")).isEqualTo(expectedWithMilliseconds)
        assertThat(converter.convert("1981-03-12T10:36:25.235-07:00")).isEqualTo(expectedWithMilliseconds)
        assertThat(converter.convert("1981-03-12T10:36:25.235-0700")).isEqualTo(expectedWithMilliseconds)
    }

    @Test
    fun convert_string_to_date_with_custom_pattern() {
        val converter = StringToDateConverter("yyyy-MM-dd HH:mm:ss")

        // 1981-03-12 10:36:25
        val expected = createDate(1981, Calendar.MARCH, 12, 10, 36, 25, 0)

        assertThat(converter.convert("1981-03-12 10:36:25")).isEqualTo(expected)
    }

    private fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int, milliseconds: Int): Date? {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute, seconds)
        calendar.set(Calendar.MILLISECOND, milliseconds)
        return calendar.time
    }
}
