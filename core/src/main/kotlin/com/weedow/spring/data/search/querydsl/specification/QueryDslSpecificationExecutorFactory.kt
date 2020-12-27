package com.weedow.spring.data.search.querydsl.specification

/**
 * Factory interface to create new [QueryDslSpecificationExecutor] from an Entity Class.
 */
interface QueryDslSpecificationExecutorFactory {

    /**
     * Returns a new [QueryDslSpecificationExecutor] instance from the given Entity Class.
     *
     * @param entityClass Entity Class used to initialize the [QueryDslSpecificationExecutor]
     * @return [QueryDslSpecificationExecutor] instance
     */
    fun <T> getQueryDslSpecificationExecutor(entityClass: Class<T>): QueryDslSpecificationExecutor<T>

}