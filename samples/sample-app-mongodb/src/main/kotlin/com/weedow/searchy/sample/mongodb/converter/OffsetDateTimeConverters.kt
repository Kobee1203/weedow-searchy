package com.weedow.searchy.sample.mongodb.converter

import org.bson.Document
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.OffsetDateTime


@WritingConverter
object OffsetDateTimeToDocumentConverter : Converter<OffsetDateTime, Document> {
    override fun convert(offsetDateTime: OffsetDateTime): Document {
        return ZonedDateTimeToDocumentConverter.convert(offsetDateTime.toZonedDateTime())
    }
}

@ReadingConverter
object DocumentToOffsetDateTimeConverter : Converter<Document, OffsetDateTime> {
    override fun convert(document: Document): OffsetDateTime {
        val zonedDateTime = DocumentToZonedDateTimeConverter.convert(document)
        return zonedDateTime.toOffsetDateTime()
    }
}