package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.Path

interface QPath<T> {

    val path: Path<*>

    val propertyInfos: PropertyInfos

}
