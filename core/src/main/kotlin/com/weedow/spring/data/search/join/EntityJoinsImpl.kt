package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.query.QueryBuilder
import com.weedow.spring.data.search.query.querytype.*
import com.weedow.spring.data.search.utils.FIELD_PATH_SEPARATOR
import com.weedow.spring.data.search.utils.klogger

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

    override fun <T> getQPath(fieldPath: String, qEntityRoot: QEntityRoot<T>, queryBuilder: QueryBuilder<T>): QPath<*> {
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
            join = queryBuilder.join(qPath, entityJoin.joinType, entityJoin.fetched)
        }

        val qPath = join.get(fieldName)
        val qName = qPath.propertyInfos.qName
        val entityJoin = joins[qName]
        if (entityJoin != null) {
            queryBuilder.join(qPath, entityJoin.joinType, entityJoin.fetched)
        }
        return qPath
    }

    override fun getJoins(filter: (EntityJoin) -> Boolean): Map<String, EntityJoin> {
        return joins.filter { filter(it.value) }
    }

}