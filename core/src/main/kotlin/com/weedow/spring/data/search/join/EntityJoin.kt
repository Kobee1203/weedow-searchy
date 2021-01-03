package com.weedow.spring.data.search.join

import com.querydsl.core.JoinType

/**
 * Value object representing the join type for a specific field.
 *
 * @param fieldPath Path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
 * @param joinName Name of the join associated to the field
 * @param joinType Join type. Default is [JoinInfo.DEFAULT_JOIN_TYPE]
 * @param fetched Whether the fetch mode is enabled. Default is [JoinInfo.DEFAULT_FETCH_MODE]
 */
data class EntityJoin(
    val fieldPath: String,
    val joinName: String,
    val joinType: JoinType = JoinInfo.DEFAULT_JOIN_TYPE,
    val fetched: Boolean = JoinInfo.DEFAULT_FETCH_MODE
)