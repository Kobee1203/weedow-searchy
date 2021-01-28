package com.weedow.searchy.mongodb.converter

import org.bson.Document
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*


internal const val DATE_TIME = "dateTime"
internal const val ZONE = "zone"
internal const val OFFSET = "offset"

@WritingConverter
object ZonedDateTimeToDocumentConverter : MongoConverter<ZonedDateTime, Document> {
    override fun convert(zonedDateTime: ZonedDateTime): Document {
        val document = Document()
        document[DATE_TIME] = Date.from(zonedDateTime.toInstant())
        document[ZONE] = zonedDateTime.zone.id
        document[OFFSET] = zonedDateTime.offset.id
        return document
    }
}

@ReadingConverter
object DocumentToZonedDateTimeConverter : MongoConverter<Document, ZonedDateTime> {
    override fun convert(document: Document): ZonedDateTime {
        val dateTime: Date = document.getDate(DATE_TIME)
        val zoneId = document.getString(ZONE)
        val zone = ZoneId.of(zoneId)
        val offsetId = document.getString(OFFSET)
        val offset = ZoneOffset.of(offsetId)
        return ZonedDateTime.ofInstant(dateTime.toInstant(), zone)
    }
}