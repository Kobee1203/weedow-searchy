package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import com.weedow.searchy.mongodb.domain.MongoPersistable
import org.springframework.core.Ordered

/**
 * [PrePersistEntityHandler][com.weedow.searchy.mongodb.event.PrePersistEntityHandler] implementation to handle the Entities that extends [MongoPersistable]
 * and the type of Id is `Long`.
 *
 * This handler increments automatically the Long Id and write this value into the new Entity's Id.
 *
 * @param dbSequenceGeneratorService [DbSequenceGeneratorService] used to get the next sequence for the given Entity
 */
class LongIdMongoPersistablePrePersistEntityHandler(
    dbSequenceGeneratorService: DbSequenceGeneratorService
) : AbstractLongIdPrePersistEntityHandler(dbSequenceGeneratorService) {
    override fun supports(entity: Any): Boolean {
        return entity is MongoPersistable<*>
                && Long::class.javaObjectType.isAssignableFrom(entity.idClass)
                && entity.id == null
    }

    @Suppress("UNCHECKED_CAST")
    override fun doHandle(entity: Any, generatedSequence: Long) {
        val src = entity as MongoPersistable<Long>
        src.setId(generatedSequence)
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE + 20
    }

}