package com.weedow.searchy.mongodb.domain

import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update

/**
 * Implementation of [DbSequenceGeneratorService] for MongoDB.
 *
 * Finds the last registered sequence for the given sequence name, updates it in increments of `1`, and returns this new value.
 * If the sequence does not exist in database, it creates a new entry and return `1`.
 *
 * @param mongoOperations [MongoOperations]
 */
class DbSequenceGeneratorServiceImpl(
    private val mongoOperations: MongoOperations
) : DbSequenceGeneratorService {
    override fun getNextSequence(seqName: String): Long {
        val counter = mongoOperations.findAndModify(
            query(where("_id").`is`(seqName)),
            Update().inc(DbSequence.SEQUENCE_FIELD_NAME, 1),
            options().returnNew(true).upsert(true),
            DbSequence::class.java
        )
        return counter?.sequence ?: 1
    }
}