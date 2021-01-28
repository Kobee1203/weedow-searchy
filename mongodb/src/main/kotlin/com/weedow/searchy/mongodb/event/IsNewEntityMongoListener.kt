package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.annotation.PrePersist
import com.weedow.searchy.mongodb.annotation.PreUpdate
import org.apache.commons.lang3.reflect.MethodUtils
import org.springframework.data.mapping.context.PersistentEntities
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent


class IsNewEntityMongoListener(
    private val persistentEntities: PersistentEntities,
    private val prePersistEntityHandlers: List<PrePersistEntityHandler>,
    private val preUpdateEntityHandlers: List<PreUpdateEntityHandler>
) : AbstractMongoEventListener<Any>() {
    override fun onBeforeConvert(event: BeforeConvertEvent<Any>) {
        val source = event.source

        val entity = persistentEntities.getRequiredPersistentEntity(source.javaClass)
        if (entity.isNew(source)) {
            val prePersistMethods = MethodUtils.getMethodsWithAnnotation(source.javaClass, PrePersist::class.java, true, true)
            prePersistMethods?.forEach { it.invoke(source) }

            prePersistEntityHandlers.forEach {
                if (it.supports(source)) {
                    it.handle(source)
                }
            }
        } else {
            val preUpdateMethods = MethodUtils.getMethodsWithAnnotation(source.javaClass, PreUpdate::class.java, true, true)
            preUpdateMethods?.forEach { it.invoke(source) }

            preUpdateEntityHandlers.forEach {
                if (it.supports(source)) {
                    it.handle(source)
                }
            }
        }
    }
}