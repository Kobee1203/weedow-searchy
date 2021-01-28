package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import org.springframework.core.Ordered

abstract class AbstractLongIdPrePersistEntityHandler(
    private val dbSequenceGeneratorService: DbSequenceGeneratorService
) : PrePersistEntityHandler, Ordered {

    final override fun handle(entity: Any) {
        val generatedSequence = generateSequence(entity)
        doHandle(entity, generatedSequence)
    }

    abstract fun doHandle(entity: Any, generatedSequence: Long)

    protected fun generateSequence(entity: Any): Long {
        return dbSequenceGeneratorService.getNextSequence(entity.javaClass.simpleName.toLowerCase() + "_sequence")
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
}