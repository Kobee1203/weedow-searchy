package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.join.handler.DefaultEntityJoinHandler
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import com.weedow.spring.data.search.querydsl.querytype.ElementType
import com.weedow.spring.data.search.utils.klogger
import java.util.*

/**
 * Default [EntityJoinManager] implementation.
 *
 * This implementation computes the Entity joins for a given [SearchDescriptor], and cache the result.
 *
 * @param dataSearchContext [DataSearchContext]
 */
class EntityJoinManagerImpl(private val dataSearchContext: DataSearchContext) : EntityJoinManager {

    private val joinsBySearchDescriptorId: MutableMap<String, EntityJoins> = HashMap()

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized EntityJoinManager: {}", this::class.qualifiedName)
    }

    override fun <T> computeEntityJoins(searchDescriptor: SearchDescriptor<T>): EntityJoins {
        return joinsBySearchDescriptorId.getOrPut(searchDescriptor.id) { initEntityJoins(searchDescriptor) }
    }

    private fun <T> initEntityJoins(searchDescriptor: SearchDescriptor<T>): EntityJoins {
        val entityJoinHandlers = mutableListOf(*searchDescriptor.entityJoinHandlers.toTypedArray())
        entityJoinHandlers.add(DefaultEntityJoinHandler())

        val entityJoins = EntityJoinsImpl(searchDescriptor.entityClass)
        doInitEntityJoins(searchDescriptor.entityClass, "", entityJoins, entityJoinHandlers)

        return entityJoins
    }

    private fun doInitEntityJoins(
        entityClass: Class<*>,
        parentPath: String,
        entityJoins: EntityJoinsImpl,
        entityJoinHandlers: List<EntityJoinHandler>
    ) {
        dataSearchContext.getAllPropertyInfos(entityClass).forEach { propertyInfos ->

            val hasJoinAnnotation = propertyInfos.annotations.any { dataSearchContext.isJoinAnnotation(it.annotationClass.java) }

            // Ignore joins for a field without a Join Annotation
            if (hasJoinAnnotation) {
                val fieldPath = EntityJoinUtils.getFieldPath(parentPath, propertyInfos.fieldName)

                // Ignore joins for a field having the same class as the root class or an entity already processed
                if (entityJoins.alreadyProcessed(propertyInfos)) {
                    return
                }

                for (entityJoinHandler in entityJoinHandlers) {
                    if (entityJoinHandler.supports(propertyInfos)) {
                        val joinInfo = entityJoinHandler.handle(propertyInfos)

                        val entityJoin = EntityJoin(fieldPath, propertyInfos.fieldName, propertyInfos.qName, joinInfo.joinType, joinInfo.fetched)
                        entityJoins.add(entityJoin)

                        // Recursive loop to handle nested Entity joins
                        val fieldClass = when (propertyInfos.elementType) {
                            ElementType.SET,
                            ElementType.LIST,
                            ElementType.COLLECTION,
                            -> {
                                propertyInfos.parameterizedTypes[0]
                            }
                            else -> propertyInfos.type
                        }
                        if (dataSearchContext.isEntity(fieldClass)) {
                            doInitEntityJoins(fieldClass, entityJoin.fieldPath, entityJoins, entityJoinHandlers)
                        }

                        break
                    }
                }
            }
        }
    }

}
