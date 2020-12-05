package com.weedow.spring.data.search.join

import com.querydsl.core.JoinType
import java.lang.reflect.Field

/**
 * Value object representing the join type for a specific field.
 *
 * @param fieldPath Path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
 * @param fieldName Name of the field
 * @param joinName Name of the join associated to the field
 * @param joinType Join type. Default is [JoinInfo.DEFAULT_JOIN_TYPE]
 * @param fetched Whether the fetch mode is enabled. Default is [JoinInfo.DEFAULT_FETCH_MODE]
 */
data class EntityJoin(
        val fieldPath: String,
        val fieldName: String,
        val joinName: String,
        val joinType: JoinType = JoinInfo.DEFAULT_JOIN_TYPE,
        val fetched: Boolean = JoinInfo.DEFAULT_FETCH_MODE,
) {

    /**
     * Secondary constructor.
     */
    constructor(
            entityClass: Class<*>,
            parentPath: String,
            field: Field,
            joinType: JoinType = JoinInfo.DEFAULT_JOIN_TYPE,
            fetched: Boolean = JoinInfo.DEFAULT_FETCH_MODE,
    ) : this(EntityJoinUtils.getFieldPath(parentPath, field.name), field.name, EntityJoinUtils.getJoinName(entityClass, field), joinType, fetched)
}