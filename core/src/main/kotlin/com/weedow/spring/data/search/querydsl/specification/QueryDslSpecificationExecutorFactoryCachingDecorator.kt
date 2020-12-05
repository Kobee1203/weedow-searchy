package com.weedow.spring.data.search.querydsl.specification

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class QueryDslSpecificationExecutorFactoryCachingDecorator(
        private val decoratedQueryDslSpecificationExecutorFactory: QueryDslSpecificationExecutorFactory,
) : QueryDslSpecificationExecutorFactory {

    private val cache: ConcurrentMap<Class<*>, QueryDslSpecificationExecutor<*>> = ConcurrentHashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getQueryDslSpecificationExecutor(domainClass: Class<T>): QueryDslSpecificationExecutor<T> {
        return cache.getOrPut(domainClass) {
            decoratedQueryDslSpecificationExecutorFactory.getQueryDslSpecificationExecutor(domainClass)
        } as QueryDslSpecificationExecutor<T>
    }

}