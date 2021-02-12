package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import org.apache.commons.lang3.reflect.FieldUtils
import org.springframework.core.Ordered

/**
 * [PrePersistEntityHandler][com.weedow.searchy.mongodb.event.PrePersistEntityHandler] implementation to handle the Entities that have a `Long` Id
 * declared as simple `id` field, without annotation.
 *
 * This handler increments automatically the Long Id and write this value into the new Entity's Id.
 *
 * @param dbSequenceGeneratorService [DbSequenceGeneratorService] used to get the next sequence for the given Entity
 */
class LongIdPrePersistEntityHandler(
    dbSequenceGeneratorService: DbSequenceGeneratorService
) : AbstractLongIdPrePersistEntityHandler(dbSequenceGeneratorService) {
    override fun supports(entity: Any): Boolean {
        val idField = FieldUtils.getField(entity.javaClass, "id", true)
        return idField != null
                && Long::class.javaObjectType.isAssignableFrom(idField.type)
                && FieldUtils.readField(idField, entity, true) == null
    }

    override fun doHandle(entity: Any, generatedSequence: Long) {
        FieldUtils.writeField(entity, "id", generatedSequence, true)
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE - 20
    }

}