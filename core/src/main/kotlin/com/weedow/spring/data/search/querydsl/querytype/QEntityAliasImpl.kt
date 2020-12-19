package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.dsl.EntityPathBase

/**
 * [QEntity] implementation representing an alias.
 *
 * @param entityClass Entity Class
 * @param fieldName field name for which the alias is associated
 * @param T Entity Class
 */
class QEntityAliasImpl<T>(
    entityClass: Class<T>,
    fieldName: String
) : EntityPathBase<T>(entityClass, PathMetadataFactory.forVariable(fieldName)), QEntity<T> {

    override fun get(fieldName: String): QPath<*> = throw UnsupportedOperationException("QEntityAlias does not support get(String) method")

}