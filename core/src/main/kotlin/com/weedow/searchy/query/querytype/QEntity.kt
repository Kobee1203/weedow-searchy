package com.weedow.searchy.query.querytype

import com.querydsl.core.types.EntityPath

/**
 * Query type for an Entity.
 *
 * @param T Entity Class
 */
interface QEntity<T> : EntityPath<T> {

    /**
     * Returns the [QPath] for the given field name.
     *
     * @param fieldName fieldName
     * @return [QPath] object
     */
    fun get(fieldName: String): QPath<*>

}
