package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.utils.klogger
import org.apache.commons.lang3.reflect.FieldUtils
import java.util.*
import javax.persistence.criteria.From
import javax.persistence.criteria.Root

class EntityJoinManagerImpl : EntityJoinManager {

    private val joinsByEntity: MutableMap<Class<*>, List<FieldJoin>> = HashMap()

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized EntityJoinManager: {}", this)
    }

    override fun <T> computeJoinMap(root: Root<T>, entityClass: Class<T>, entityJoinHandlers: List<EntityJoinHandler<T>>): Map<String, From<*, *>> {
        val joinMap = mutableMapOf<String, From<*, *>>()

        val fieldJoins = joinsByEntity.getOrPut(entityClass) { initFieldJoins(entityClass, entityJoinHandlers) }

        fieldJoins.forEach { fieldJoin ->
            val fieldName: String = fieldJoin.fieldName
            val from = joinMap.getOrElse(fieldJoin.parentClass.canonicalName) { root }

            val join = if (fieldJoin.fetched) {
                if (log.isDebugEnabled) log.debug("Creating a fetch join to the ${fieldJoin.parentClass.canonicalName}.$fieldName field using the join type ${fieldJoin.joinType}")
                from.fetch<Any, Any>(fieldName, fieldJoin.joinType) as From<*, *>
            } else {
                if (log.isDebugEnabled) log.debug("Creating a join to the ${fieldJoin.parentClass.canonicalName}.$fieldName field using the join type ${fieldJoin.joinType}")
                from.join<Any, Any>(fieldName, fieldJoin.joinType)
            }
            joinMap[fieldJoin.joinName] = join
        }

        return joinMap
    }

    private fun <T> initFieldJoins(entityClass: Class<T>, entityJoinHandlers: List<EntityJoinHandler<T>>): List<FieldJoin> {
        val fieldJoins = mutableListOf<FieldJoin>()
        doInitFieldJoins(entityClass, entityClass, entityJoinHandlers, fieldJoins)
        return fieldJoins
    }

    private fun <T> doInitFieldJoins(rootClass: Class<T>, entityClass: Class<*>, entityJoinHandlers: List<EntityJoinHandler<T>>, fieldJoins: MutableList<FieldJoin>) {
        for (field in FieldUtils.getAllFieldsList(entityClass)) {
            FieldJoinInfo(rootClass, entityClass, field)
                    .canHandleJoins(fieldJoins) { fieldJoinInfo ->
                        doInitFieldJoins(fieldJoinInfo, entityJoinHandlers, fieldJoins)
                    }
        }
    }

    private fun <T> doInitFieldJoins(fieldJoinInfo: FieldJoinInfo<T>, entityJoinHandlers: List<EntityJoinHandler<T>>, fieldJoins: MutableList<FieldJoin>) {
        entityJoinHandlers.forEach { entityJoinHandler ->
            if (entityJoinHandler.supports(fieldJoinInfo)) {
                val fieldJoin = entityJoinHandler.handle(fieldJoinInfo)
                fieldJoins.add(fieldJoin)

                // Recursive loop to handle the children entities
                doInitFieldJoins(fieldJoinInfo.rootClass, fieldJoinInfo.fieldClass, entityJoinHandlers, fieldJoins)

                return
            }
        }
    }
}
