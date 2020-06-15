package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.FIELD_PATH_SEPARATOR
import com.weedow.spring.data.search.utils.klogger
import org.hibernate.query.criteria.internal.JoinImplementor
import java.lang.reflect.Field
import javax.persistence.criteria.From
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root

class EntityJoinsImpl(private val rootClass: Class<*>) : EntityJoins {

    private val joins = mutableMapOf<String, EntityJoin>()

    companion object {
        private val log by klogger()
    }

    fun alreadyProcessed(entityClass: Class<*>, field: Field): Boolean {
        // Do not create a join with the root Entity class
        if (this.rootClass == EntityUtils.getFieldClass(field)) {
            return true
        }

        val joinName = EntityJoinUtils.getJoinName(entityClass, field)
        return joins.keys.stream().anyMatch { it == joinName }
    }

    fun add(entityJoin: EntityJoin) {
        joins[entityJoin.joinName] = entityJoin
    }

    override fun <T> getPath(fieldPath: String, root: Root<T>): Path<*> {
        val parts = fieldPath.split(FIELD_PATH_SEPARATOR)
        val fieldName = parts[parts.size - 1]
        val parents = parts.subList(0, parts.size - 1)

        var parentPath = ""
        var join = root as From<*, *>
        for (parent in parents) {
            join = getOrCreateJoin(join, parentPath, parent)
            parentPath = EntityJoinUtils.getFieldPath(parentPath, parent)
        }

        val joinName = EntityJoinUtils.getJoinName(join.javaType, join.javaType.getDeclaredField(fieldName))
        val entityJoin = joins[joinName]
        if (entityJoin != null) {
            getOrCreateJoin(join, parentPath, fieldName)
        }

        return join.get<Any>(fieldName)
    }

    override fun getJoins(filter: (EntityJoin) -> Boolean): Map<String, EntityJoin> {
        return joins.filter { filter(it.value) }
    }

    private fun getOrCreateJoin(from: From<*, *>, parentPath: String, attributeName: String): JoinImplementor<*, *> {
        val entityClass = from.javaType
        val field = entityClass.getDeclaredField(attributeName)

        val joinName = EntityJoinUtils.getJoinName(entityClass, field)
        val entityJoin = joins.getOrElse(joinName) {
            EntityJoin(entityClass, parentPath, field)
        }

        for (join in from.joins) {
            if (join.attribute.name == entityJoin.fieldName && join.joinType == entityJoin.joinType) {
                return join as JoinImplementor<*, *>
            }
        }
        for (join in from.fetches) {
            if (join.attribute.name == entityJoin.fieldName && join.joinType == entityJoin.joinType) {
                return join as JoinImplementor<*, *>
            }
        }

        return createJoin(entityJoin, from, attributeName)
    }

    private fun createJoin(entityJoin: EntityJoin, from: From<*, *>, attributeName: String): JoinImplementor<*, *> {
        val join = if (entityJoin.fetched) {
            if (log.isDebugEnabled) log.debug("Creating a fetch join to the ${from.javaType.canonicalName}.$attributeName field using the join type ${entityJoin.joinType}")
            from.fetch<Any, Any>(attributeName, entityJoin.joinType) as JoinImplementor<*, *>
        } else {
            if (log.isDebugEnabled) log.debug("Creating a join to the ${from.javaType.canonicalName}.$attributeName field using the join type ${entityJoin.joinType}")
            from.join<Any, Any>(attributeName, entityJoin.joinType) as JoinImplementor<*, *>
        }
        return join
    }

}