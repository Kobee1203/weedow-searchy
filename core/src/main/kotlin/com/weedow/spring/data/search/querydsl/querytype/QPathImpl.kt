package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.Path

data class QPathImpl<T>(
        override val path: Path<T>,
        override val propertyInfos: PropertyInfos,
) : QPath<T>