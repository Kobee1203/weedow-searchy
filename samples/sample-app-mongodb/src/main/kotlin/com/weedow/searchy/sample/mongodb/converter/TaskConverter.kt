package com.weedow.searchy.sample.mongodb.converter

import com.weedow.searchy.mongodb.converter.MongoConverter
import com.weedow.searchy.sample.mongodb.model.Task
import org.bson.Document
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
object TaskToDocumentConverter : MongoConverter<Task, Document> {
    override fun convert(task: Task): Document {
        val document = Document()
        document["_id"] = task.getId()
        //document["createdOn"] = OffsetDateTimeToDocumentConverter.convert(task.getCreatedOn())
        //document["updatedOn"] = OffsetDateTimeToDocumentConverter.convert(task.getUpdatedOn())
        document["name"] = task.name
        document["description"] = task.description
        return document
    }
}

@ReadingConverter
object DocumentToTaskConverter : MongoConverter<Document, Task> {
    override fun convert(document: Document): Task {
        val task = Task(document.getString("name"), document.getString("description"))
        task.setId(document.getLong("_id"))
        //task.getCreatedOn() = DocumentToOffsetDateTimeConverter.convert(document.get("createdOn"))
        //task.getUpdatedOn() = DocumentToOffsetDateTimeConverter.convert(document.get("updatedOn"))
        return task
    }
}