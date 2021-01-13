package com.weedow.searchy.query.specification

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * [SpecificationExecutorFactory] implementation which wraps a given [SpecificationExecutorFactory]
 * and cache the result of the [getSpecificationExecutor] method.
 *
 * @param decoratedSpecificationExecutorFactory [SpecificationExecutorFactory] to be wrapped
 */
class SpecificationExecutorFactoryCachingDecorator(
    private val decoratedSpecificationExecutorFactory: SpecificationExecutorFactory
) : SpecificationExecutorFactory {

    private val cache: ConcurrentMap<Class<*>, SpecificationExecutor<*>> = ConcurrentHashMap()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getSpecificationExecutor(entityClass: Class<T>): SpecificationExecutor<T> {
        return cache.getOrPut(entityClass) {
            decoratedSpecificationExecutorFactory.getSpecificationExecutor(entityClass)
        } as SpecificationExecutor<T>
    }

}