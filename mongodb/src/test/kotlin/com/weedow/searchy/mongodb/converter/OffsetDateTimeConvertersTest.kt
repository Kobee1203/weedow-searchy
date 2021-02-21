package com.weedow.searchy.mongodb.converter

import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class OffsetDateTimeConvertersTest {

    companion object {
        val DATE_VALUE = "2020-03-12T09:36:00Z"
        val OFFSET_VALUE = "+01:00"
        val JSON = "{" +
                "\"$DATE_TIME\": {\"\$date\": \"$DATE_VALUE\"}, " +
                "\"$ZONE\": \"$OFFSET_VALUE\", " +
                "\"$OFFSET\": \"$OFFSET_VALUE\"" +
                "}"
        val OFFSET_DATE_TIME = OffsetDateTime.of(2020, 3, 12, 10, 36, 0, 0, ZoneOffset.of(OFFSET_VALUE))
    }

    @Nested
    inner class OffsetDateTimeToDocumentConverterTest {

        @Test
        fun test() {
            val document = OffsetDateTimeToDocumentConverter.convert(OFFSET_DATE_TIME)

            assertThat(document.toJson()).isEqualTo(JSON)
        }

    }

    @Nested
    inner class DocumentToOffsetDateTimeConverterTest {

        @Test
        fun test() {
            val document = Document.parse(JSON)
            val OffsetDateTime = DocumentToOffsetDateTimeConverter.convert(document)
            assertThat(OffsetDateTime).isEqualTo(OFFSET_DATE_TIME)
        }

    }
}