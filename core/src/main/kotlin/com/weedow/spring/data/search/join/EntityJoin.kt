package com.weedow.spring.data.search.join

import java.lang.reflect.Field
import javax.persistence.criteria.JoinType

data class EntityJoin(val fieldPath: String,
                      val joinName: String,
                      val joinType: JoinType = JoinType.INNER,
                      val fetched: Boolean = false) {

    constructor(entityClass: Class<*>,
                parentPath: String,
                field: Field,
                joinType: JoinType = JoinType.INNER,
                fetched: Boolean = false
    ) : this(EntityJoinUtils.getFieldPath(parentPath, field.name), EntityJoinUtils.getJoinName(entityClass, field), joinType, fetched) {
    }
}