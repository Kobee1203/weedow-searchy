package com.weedow.searchy.query.specification

/**
 * Factory interface to create new [SpecificationExecutor] from an Entity Class.
 */
interface SpecificationExecutorFactory {

    /**
     * Returns a new [SpecificationExecutor] instance from the given Entity Class.
     *
     * @param entityClass Entity Class used to initialize the [SpecificationExecutor]
     * @return [SpecificationExecutor] instance
     */
    fun <T> getSpecificationExecutor(entityClass: Class<T>): SpecificationExecutor<T>

}