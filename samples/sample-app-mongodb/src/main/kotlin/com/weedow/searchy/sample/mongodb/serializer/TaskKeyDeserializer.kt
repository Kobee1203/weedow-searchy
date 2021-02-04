package com.weedow.searchy.sample.mongodb.serializer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.weedow.searchy.sample.mongodb.model.Task

class TaskKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext?): Any {
        val map = ObjectMapper().readValue(key, object : TypeReference<Map<String, Any?>>() {})
        val task = Task(map["name"].toString(), map["description"].toString())
        task.setId(map["id"].toString().toLong())
        return task
    }
}