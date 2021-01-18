package com.weedow.searchy.sample.mongodb.serializer

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer

class TaskKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String?, ctxt: DeserializationContext?): Any {
        TODO("Not yet implemented")
    }
}