package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.domain.MongoPersistable
import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import org.springframework.core.Ordered

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