package com.weedow.searchy.mongodb.converter

import org.bson.Document
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.OffsetDateTime


/**
 * Simple singleton to convert an [OffsetDateTime] to a [Document].
 */
@WritingConverter
object OffsetDateTimeToDocumentConverter : MongoConverter<OffsetDateTime, Document> {
    override fun convert(offsetDateTime: OffsetDateTime): Document {
        return ZonedDateTimeToDocumentConverter.convert(offsetDateTime.toZonedDateTime())
    }
}

/**
 * Simple singleton to convert a [Document] to an [OffsetDateTime].
 */
@ReadingConverter
object DocumentToOffsetDateTimeConverter : MongoConverter<Document, OffsetDateTime> {
    override fun convert(document: Document): OffsetDateTime {
        val zonedDateTime = DocumentToZonedDateTimeConverter.convert(document)
        return zonedDateTime.toOffsetDateTime()
    }
}