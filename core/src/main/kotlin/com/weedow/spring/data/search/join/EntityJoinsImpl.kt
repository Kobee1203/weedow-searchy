package com.weedow.spring.data.search.join

import com.querydsl.core.JoinType
import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.querytype.*
import com.weedow.spring.data.search.utils.FIELD_PATH_SEPARATOR
import com.weedow.spring.data.search.utils.MAP_KEY
import com.weedow.spring.data.search.utils.MAP_VALUE
import com.weedow.spring.data.search.utils.klogger
import org.hibernate.query.criteria.internal.JoinImplementor
import javax.persistence.criteria.From
import javax.persistence.criteria.MapJoin
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root

/**
 * Default [EntityJoins] implementation.
 */
class EntityJoinsImpl(private val rootClass: Class<*>) : EntityJoins {

    private val joins = mutableMapOf<String, EntityJoin>()

    companion object {
        private val log by klogger()
    }

    /**
     * Check if the class of the given field has been already processed, and so prevent to store the same [EntityJoin] more than once.
     *
     * If the field class is the same class as the root entity, the field is considered as already processed.
     *
     * @param propertyInfos Information about an Entity field declared with a Join Annotation
     * @return `true` if the property has been already processed, `false` instead
     */
    fun alreadyProcessed(propertyInfos: PropertyInfos): Boolean {
        val fieldClass = when (propertyInfos.elementType) {
            ElementType.SET,
            ElementType.LIST,
            ElementType.COLLECTION,
            -> {
                propertyInfos.parameterizedTypes[0]
            }
            else -> propertyInfos.type
        }

        // Do not create a join with the root Entity class
        if (this.rootClass == fieldClass) {
            return true
        }

        val joinName = propertyInfos.qName
        return joins.keys.stream().anyMatch { it == joinName }
    }

    /**
     * Add a new [EntityJoin].
     */
    fun add(entityJoin: EntityJoin) {
        joins[entityJoin.joinName] = entityJoin
    }

    override fun <T> getQPath(fieldPath: String, qEntityRoot: QEntityRoot<T>, queryDslBuilder: QueryDslBuilder<T>): QPath<*> {
        val parts = fieldPath.split(FIELD_PATH_SEPARATOR)
        val fieldName = parts[parts.size - 1]
        val parents = parts.subList(0, parts.size - 1)

        var join: QEntity<*> = qEntityRoot
        for (parent in parents) {
            val qPath = join.get(parent)
            val qName = qPath.propertyInfos.qName
            val entityJoin = joins.getOrElse(qName) {
                EntityJoin(qPath.path.toString(), parent, qName)
            }
            join = queryDslBuilder.join(qPath, entityJoin.joinType, entityJoin.fetched)
        }

        return join.get(fieldName)
    }

    override fun <T> getPath(fieldPath: String, root: Root<T>): Path<*> {
        val parts = fieldPath.split(FIELD_PATH_SEPARATOR)
        val fieldName = parts[parts.size - 1]
        val parents = parts.subList(0, parts.size - 1)

        var parentPath = ""
        var join = root as Path<*>
        for (parent in parents) {
            join = if (MapJoin::class.java.isAssignableFrom(join.javaClass)) {
                getMapJoinPath(join as MapJoin<*, *, *>, parent)
            } else {
                getOrCreateJoin(join as From<*, *>, parentPath, parent)
            }
            parentPath = EntityJoinUtils.getFieldPath(parentPath, parent)
        }

        return if (MapJoin::class.java.isAssignableFrom(join.javaClass)) {
            val mapJoin = join as MapJoin<*, *, *>
            getMapJoinPath(mapJoin, fieldName)
        } else {
            val joinName = EntityJoinUtils.getJoinName(join.javaType, join.javaType.getDeclaredField(fieldName))
            val entityJoin = joins[joinName]
            if (entityJoin != null) {
                getOrCreateJoin(join as From<*, *>, parentPath, fieldName)
            }

            join.get<Any>(fieldName)
        }
    }

    override fun getJoins(filter: (EntityJoin) -> Boolean): Map<String, EntityJoin> {
        return joins.filter { filter(it.value) }
    }

    private fun getMapJoinPath(mapJoin: MapJoin<*, *, *>, attributeName: String): Path<*> {
        return when (attributeName) {
            MAP_KEY -> mapJoin.key()
            MAP_VALUE -> mapJoin.value()
            else -> throw IllegalArgumentException("The attribute name '$attributeName' is not authorized for a parent Map Join") /* mapJoin.get<Any>(attributeName) */
        }
    }

    private fun getOrCreateJoin(from: From<*, *>, parentPath: String, attributeName: String): JoinImplementor<*, *> {
        val entityClass = from.javaType

        val field = entityClass.getDeclaredField(attributeName)

        val joinName = EntityJoinUtils.getJoinName(entityClass, field)
        val entityJoin = joins.getOrElse(joinName) {
            val fieldPath = EntityJoinUtils.getFieldPath(parentPath, field.name)
            EntityJoin(fieldPath, field.name, joinName)
        }

        val joinType = toJpaJoinType(entityJoin.joinType)
        for (join in from.joins) {
            if (join.attribute.name == entityJoin.fieldName && join.joinType == joinType) {
                return join as JoinImplementor<*, *>
            }
        }
        for (join in from.fetches) {
            if (join.attribute.name == entityJoin.fieldName && join.joinType == joinType) {
                return join as JoinImplementor<*, *>
            }
        }

        return createJoin(entityJoin, from, attributeName)
    }

    private fun createJoin(entityJoin: EntityJoin, from: From<*, *>, attributeName: String): JoinImplementor<*, *> {
        val joinType = toJpaJoinType(entityJoin.joinType)
        val join = if (entityJoin.fetched) {
            if (log.isDebugEnabled) log.debug("Creating a fetch join to the ${from.javaType.canonicalName}.$attributeName field using the join type $joinType")
            from.fetch<Any, Any>(attributeName, joinType) as JoinImplementor<*, *>
        } else {
            if (log.isDebugEnabled) log.debug("Creating a join to the ${from.javaType.canonicalName}.$attributeName field using the join type $joinType")
            from.join<Any, Any>(attributeName, joinType) as JoinImplementor<*, *>
        }
        return join
    }

    private fun toJpaJoinType(joinType: JoinType): javax.persistence.criteria.JoinType {
        return when (joinType) {
            JoinType.JOIN -> javax.persistence.criteria.JoinType.INNER
            JoinType.INNERJOIN -> javax.persistence.criteria.JoinType.INNER
            JoinType.LEFTJOIN -> javax.persistence.criteria.JoinType.LEFT
            JoinType.RIGHTJOIN -> javax.persistence.criteria.JoinType.RIGHT
            else -> throw IllegalArgumentException("Join Type not supported in JPA: $joinType")
        }
    }

}