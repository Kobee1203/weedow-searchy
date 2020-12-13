package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.dsl.EntityPathBase

class QEntityAliasImpl<T>(
    private val entityClass: Class<T>,
    private val fieldName: String,
) : EntityPathBase<T>(entityClass, PathMetadataFactory.forVariable(fieldName)), QEntity<T> {

    override fun get(fieldName: String): QPath<*> = throw UnsupportedOperationException("QEntityAlias does not support get(String) method")

}