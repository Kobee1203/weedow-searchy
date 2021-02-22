package com.weedow.searchy.mongodb.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 * MongoDB Entity to store the [Long] sequences of the entities having a Long Id.
 */
@Document("db_sequences")
class DbSequence(
    @Id
    val id: String,

    @Indexed
    @Field(SEQUENCE_FIELD_NAME)
    val sequence: Long
) {
    companion object {
        const val SEQUENCE_FIELD_NAME = "value"
    }
}