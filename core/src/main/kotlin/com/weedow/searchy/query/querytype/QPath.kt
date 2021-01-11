package com.weedow.searchy.query.querytype

import com.querydsl.core.types.Path

/**
 * Interface with the data related to a property retrieved from [QEntity.get(String][QEntity.get].
 */
interface QPath<T> {

    /** Path represents a path expression. Paths refer to variables, properties and collection members access. */
    val path: Path<T>

    /** Property information */
    val propertyInfos: PropertyInfos

}
