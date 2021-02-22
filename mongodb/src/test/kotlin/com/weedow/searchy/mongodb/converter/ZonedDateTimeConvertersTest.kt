package com.weedow.searchy.mongodb.converter

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

internal class ZonedDateTimeConvertersTest {

    companion object {
        val DATE_VALUE = "2020-03-12T09:36:00Z"
        val ZONE_ID_VALUE = "Europe/Paris"
        val OFFSET_VALUE = "+01:00"
        val JSON = "{" +
                "\"$DATE_TIME\": {\"\$date\": \"$DATE_VALUE\"}, " +
                "\"$ZONE\": \"$ZONE_ID_VALUE\", " +
                "\"$OFFSET\": \"$OFFSET_VALUE\"" +
                "}"
        val ZONED_DATE_TIME = ZonedDateTime.of(2020, 3, 12, 10, 36, 0, 0, ZoneId.of(ZONE_ID_VALUE))
    }

    @Nested
    inner class ZonedDateTimeToDocumentConverterTest {

        @Test
        fun test() {
            val document = ZonedDateTimeToDocumentConverter.convert(ZONED_DATE_TIME)

            assertThat(document.toJson()).isEqualTo(JSON)
        }

    }

    @Nested
    inner class DocumentToZonedDateTimeConverterTest {

        @Test
        fun test() {
            val document = Document.parse(JSON)
            val zonedDateTime = DocumentToZonedDateTimeConverter.convert(document)
            assertThat(zonedDateTime).isEqualTo(ZONED_DATE_TIME)
        }

    }
}