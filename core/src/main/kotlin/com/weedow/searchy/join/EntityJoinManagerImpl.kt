package com.weedow.searchy.join

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.join.handler.DefaultEntityJoinHandler
import com.weedow.searchy.join.handler.EntityJoinHandler
import com.weedow.searchy.query.querytype.ElementType
import com.weedow.searchy.utils.klogger
import java.util.*

/**
 * Default [EntityJoinManager] implementation.
 *
 * This implementation computes the Entity joins for a given [SearchyDescriptor], and cache the result.
 *
 * @param searchyContext [SearchyContext]
 */
class EntityJoinManagerImpl(private val searchyContext: SearchyContext) : EntityJoinManager {

    private val joinsBySearchyDescriptorId: MutableMap<String, EntityJoins> = HashMap()

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized EntityJoinManager: {}", this::class.qualifiedName)
    }

    override fun <T> computeEntityJoins(searchyDescriptor: SearchyDescriptor<T>): EntityJoins {
        return joinsBySearchyDescriptorId.getOrPut(searchyDescriptor.id) { initEntityJoins(searchyDescriptor) }
    }

    private fun <T> initEntityJoins(searchyDescriptor: SearchyDescriptor<T>): EntityJoins {
        val entityJoinHandlers = mutableListOf(*searchyDescriptor.entityJoinHandlers.toTypedArray())
        entityJoinHandlers.add(DefaultEntityJoinHandler())

        val entityJoins = EntityJoinsImpl(searchyDescriptor.entityClass)
        doInitEntityJoins(searchyDescriptor.entityClass, "", entityJoins, entityJoinHandlers)

        return entityJoins
    }

    private fun doInitEntityJoins(
        entityClass: Class<*>,
        parentPath: String,
        entityJoins: EntityJoinsImpl,
        entityJoinHandlers: List<EntityJoinHandler>
    ) {
        val allPropertyInfos = searchyContext.getAllPropertyInfos(entityClass)
        for (propertyInfos in allPropertyInfos) {
            val hasJoinAnnotation = propertyInfos.annotations.any { searchyContext.isJoinAnnotation(it.annotationClass.java) }

            // Ignore joins for a field without a Join Annotation
            if (hasJoinAnnotation) {
                val fieldPath = EntityJoinUtils.getFieldPath(parentPath, propertyInfos.fieldName)

                // Ignore joins for a field having the same class as the root class or an entity already processed
                if (entityJoins.alreadyProcessed(propertyInfos)) {
                    continue
                }

                for (entityJoinHandler in entityJoinHandlers) {
                    if (entityJoinHandler.supports(propertyInfos)) {
                        val joinInfo = entityJoinHandler.handle(propertyInfos)

                        val entityJoin = EntityJoin(fieldPath, propertyInfos.qName, joinInfo.joinType, joinInfo.fetched)
                        entityJoins.add(entityJoin)

                        // Recursive loop to handle nested Entity joins
                        val fieldClass = when (propertyInfos.elementType) {
                            ElementType.SET,
                            ElementType.LIST,
                            ElementType.COLLECTION,
                            -> {
                                propertyInfos.parameterizedTypes[0]
                            }
                            ElementType.MAP -> propertyInfos.parameterizedTypes[1]
                            else -> propertyInfos.type
                        }
                        if (searchyContext.isEntity(fieldClass)) {
                            doInitEntityJoins(fieldClass, entityJoin.fieldPath, entityJoins, entityJoinHandlers)
                        }

                        break
                    }
                }
            }
        }
    }

}
