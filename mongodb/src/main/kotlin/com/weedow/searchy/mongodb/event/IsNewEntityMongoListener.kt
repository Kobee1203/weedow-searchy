package com.weedow.searchy.mongodb.event

import com.weedow.searchy.mongodb.annotation.PrePersist
import com.weedow.searchy.mongodb.annotation.PreUpdate
import org.apache.commons.lang3.reflect.MethodUtils
import org.springframework.data.mapping.context.PersistentEntities
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent


/**
 * [ApplicationListener][org.springframework.context.ApplicationListener] for Mongo to handle [PrePersist]/[PreUpdate] callbacks.
 *
 * * If it is a new Entity:
 *     * it looks for the methods with [PrePersist] annotation, and execute them
 *     * It loops over the [prePersistEntityHandlers], and calls the `handle` method for the handlers that support the Entity
 * * If it is not a new Entity (Update):
 *     * it looks for the methods with [PreUpdate] annotation, and execute them
 *     * It loops over the [preUpdateEntityHandlers], and calls the `handle` method for the handlers that support the Entity
 *
 * @param persistentEntities [PersistentEntities] used to retrieve the [PersistentEntity][org.springframework.data.mapping.PersistentEntity] for the domain type found in the given event.
 * @param prePersistEntityHandlers List of [PrePersistEntityHandler]s
 * @param preUpdateEntityHandlers List of [PreUpdateEntityHandler]s
 */
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