package com.weedow.searchy.mongodb.query.querytype

import com.querydsl.core.types.Path
import com.weedow.searchy.query.querytype.ElementType

/**
 * Interface that extends [Path] and exposes the related [ElementType].
 */
interface PathWrapper<T> : Path<T> {
    val elementType: ElementType
}