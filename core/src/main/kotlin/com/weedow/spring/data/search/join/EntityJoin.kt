package com.weedow.spring.data.search.join

import java.lang.reflect.Field
import javax.persistence.criteria.JoinType

/**
 * Value object representing the join type for a specific field.
 */
data class EntityJoin(val fieldPath: String,
                      val fieldName: String,
                      val joinName: String,
                      val joinType: JoinType = JoinInfo.DEFAULT_JOIN_TYPE,
                      val fetched: Boolean = JoinInfo.DEFAULT_FETCH_MODE) {

    constructor(entityClass: Class<*>,
                parentPath: String,
                field: Field,
                joinType: JoinType = JoinInfo.DEFAULT_JOIN_TYPE,
                fetched: Boolean = JoinInfo.DEFAULT_FETCH_MODE
    ) : this(EntityJoinUtils.getFieldPath(parentPath, field.name), field.name, EntityJoinUtils.getJoinName(entityClass, field), joinType, fetched) {
    }
}