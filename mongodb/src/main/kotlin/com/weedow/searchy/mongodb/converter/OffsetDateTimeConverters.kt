package com.weedow.searchy.mongodb.converter

import org.bson.Document
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.OffsetDateTime


@WritingConverter
object OffsetDateTimeToDocumentConverter : MongoConverter<OffsetDateTime, Document> {
    override fun convert(offsetDateTime: OffsetDateTime): Document {
        return ZonedDateTimeToDocumentConverter.convert(offsetDateTime.toZonedDateTime())
    }
}

@ReadingConverter
object DocumentToOffsetDateTimeConverter : MongoConverter<Document, OffsetDateTime> {
    override fun convert(document: Document): OffsetDateTime {
        val zonedDateTime = DocumentToZonedDateTimeConverter.convert(document)
        return zonedDateTime.toOffsetDateTime()
    }
}