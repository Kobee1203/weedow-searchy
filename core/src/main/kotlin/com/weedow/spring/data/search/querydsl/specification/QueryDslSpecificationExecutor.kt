package com.weedow.spring.data.search.querydsl.specification

/**
 * Interface to allow execution of [QueryDslSpecification]s based on the QueryDsl API.
 */
interface QueryDslSpecificationExecutor<T> {

    /**
     * Returns all entities matching the given [QueryDslSpecification].
     *
     * @param specification can be `null`
     * @return List of entities. Never `null`
     */
    fun findAll(specification: QueryDslSpecification<T>?): List<T>

}