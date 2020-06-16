package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.join.handler.DefaultEntityJoinHandler
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.klogger
import org.apache.commons.lang3.reflect.FieldUtils
import java.util.*

/**
 * Default [EntityJoinManager] implementation.
 *
 * This implementation computes the Entity joins for a given [SearchDescriptor], and cache the result.
 */
class EntityJoinManagerImpl : EntityJoinManager {

    private val joinsBySearchDescriptorId: MutableMap<String, EntityJoins> = HashMap()

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized EntityJoinManager: {}", this)
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

    private fun doInitEntityJoins(entityClass: Class<*>, parentPath: String, entityJoins: EntityJoinsImpl, entityJoinHandlers: List<EntityJoinHandler<*>>) {
        for (field in FieldUtils.getAllFieldsList(entityClass)) {
            val joinAnnotation: Annotation? = EntityUtils.getJoinAnnotationClass(field)?.let { field.getAnnotation(it) }

            // Ignore joins for a field without a Join Annotation
            if (joinAnnotation != null) {
                // Ignore joins for a field having the same class as the root class or an entity already processed
                if (entityJoins.alreadyProcessed(entityClass, field)) {
                    continue
                }

                val fieldClass = EntityUtils.getFieldClass(field)

                for (entityJoinHandler in entityJoinHandlers) {
                    if (entityJoinHandler.supports(entityClass, fieldClass, field.name, joinAnnotation)) {
                        val joinInfo = entityJoinHandler.handle(entityClass, fieldClass, field.name, joinAnnotation)

                        val entityJoin = EntityJoin(entityClass, parentPath, field, joinInfo.joinType, joinInfo.fetched)
                        entityJoins.add(entityJoin)

                        // Recursive loop to handle nested joins, except ElementCollection fields
                        if (!EntityUtils.isElementCollection(field)) {
                            doInitEntityJoins(fieldClass, entityJoin.fieldPath, entityJoins, entityJoinHandlers)
                        }

                        break
                    }
                }
            }
        }
    }

}
