package com.weedow.spring.data.search.querydsl.specification

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * [QueryDslSpecificationExecutorFactory] implementation which wraps a given [QueryDslSpecificationExecutorFactory]
 * and cache the result of the [getQueryDslSpecificationExecutor] method.
 *
 * @param decoratedQueryDslSpecificationExecutorFactory [QueryDslSpecificationExecutorFactory] to be wrapped
 */
class QueryDslSpecificationExecutorFactoryCachingDecorator(
    private val decoratedQueryDslSpecificationExecutorFactory: QueryDslSpecificationExecutorFactory
) : QueryDslSpecificationExecutorFactory {

    private val cache: ConcurrentMap<Class<*>, QueryDslSpecificationExecutor<*>> = ConcurrentHashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getQueryDslSpecificationExecutor(entityClass: Class<T>): QueryDslSpecificationExecutor<T> {
        return cache.getOrPut(entityClass) {
            decoratedQueryDslSpecificationExecutorFactory.getQueryDslSpecificationExecutor(entityClass)
        } as QueryDslSpecificationExecutor<T>
    }

}