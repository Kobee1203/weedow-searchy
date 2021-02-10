package com.weedow.searchy.mongodb.query.querytype

import com.querydsl.core.types.Path
import com.weedow.searchy.query.querytype.ElementType

interface PathWrapper<T> : Path<T> {
    val elementType: ElementType
}