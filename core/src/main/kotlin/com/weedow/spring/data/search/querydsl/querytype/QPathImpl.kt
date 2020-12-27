package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.Path

/**
 * Default [QPath] implementation.
 *
 * @param path [Path]
 * @param propertyInfos [PropertyInfos]
 * @param T Expression type
 */
data class QPathImpl<T>(
    override val path: Path<T>,
    override val propertyInfos: PropertyInfos
) : QPath<T>