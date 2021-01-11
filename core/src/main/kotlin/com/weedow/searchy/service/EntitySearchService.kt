package com.weedow.searchy.service

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.expression.RootExpression

/**
 * Service interface to find all Entities according to the given [RootExpression] and the [SearchyDescriptor].
 *
 * @see RootExpression
 * @see SearchyDescriptor
 * @see com.weedow.searchy.expression.Expression
 */
interface EntitySearchService {

    /**
     * Finds all Entities from the given arguments and returns the found Entities.
     *
     * @param rootExpression [RootExpression] object that contains the criteria to filter the result
     * @param searchyDescriptor [SearchyDescriptor] object that contains the configuration for a specific Entity Class
     * @return List of Entities
     */
    fun <T> findAll(rootExpression: RootExpression<T>, searchyDescriptor: SearchyDescriptor<T>): List<T>

}