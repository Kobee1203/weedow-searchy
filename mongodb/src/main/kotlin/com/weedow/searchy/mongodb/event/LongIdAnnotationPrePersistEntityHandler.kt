package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import com.weedow.searchy.utils.EntityUtils
import org.apache.commons.lang3.reflect.FieldUtils
import org.springframework.core.Ordered
import org.springframework.data.annotation.Id

class LongIdAnnotationPrePersistEntityHandler(
    dbSequenceGeneratorService: DbSequenceGeneratorService
) : AbstractLongIdPrePersistEntityHandler(dbSequenceGeneratorService) {
    override fun supports(entity: Any): Boolean {
        val idField = EntityUtils.getFieldWithAnnotation(entity.javaClass, Id::class.java)
        return idField != null
                && Long::class.javaObjectType.isAssignableFrom(idField.type)
                && FieldUtils.readField(idField, entity, true) == null
    }

    override fun doHandle(entity: Any, generatedSequence: Long) {
        val idField = EntityUtils.getFieldWithAnnotation(entity.javaClass, Id::class.java)
        FieldUtils.writeField(idField, entity, generatedSequence, true)
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE + 100
    }

}