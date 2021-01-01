package com.weedow.spring.data.search.query.specification

/**
 * Interface to allow execution of [Specification]s.
 */
interface SpecificationExecutor<T> {

    /**
     * Returns all entities matching the given [Specification].
     *
     * @param specification can be `null`
     * @return List of entities. Never `null`
     */
    fun findAll(specification: Specification<T>?): List<T>

}