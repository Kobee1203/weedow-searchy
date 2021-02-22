package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import org.springframework.core.Ordered

/**
 * Abstract class to handle the Entities with a `Long` Id.
 *
 * @param dbSequenceGeneratorService [DbSequenceGeneratorService] used to get the next sequence and write the new value into the Entity's Id
 */
abstract class AbstractLongIdPrePersistEntityHandler(
    private val dbSequenceGeneratorService: DbSequenceGeneratorService
) : PrePersistEntityHandler, Ordered {

    final override fun handle(entity: Any) {
        val generatedSequence = generateSequence(entity)
        doHandle(entity, generatedSequence)
    }

    protected abstract fun doHandle(entity: Any, generatedSequence: Long)

    private fun generateSequence(entity: Any): Long {
        return dbSequenceGeneratorService.getNextSequence(entity.javaClass.simpleName.toLowerCase() + "_sequence")
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
}