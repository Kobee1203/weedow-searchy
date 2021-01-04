package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression

/**
 * Service interface to find all Entities according to the given [RootExpression] and the [SearchDescriptor].
 *
 * @see RootExpression
 * @see SearchDescriptor
 * @see com.weedow.spring.data.search.expression.Expression
 */
interface EntitySearchService {

    /**
     * Finds all Entities from the given arguments and returns the found Entities.
     *
     * @param rootExpression [RootExpression] object that contains the criteria to filter the result
     * @param searchDescriptor [SearchDescriptor] object that contains the configuration for a specific Entity Class
     * @return List of Entities
     */
    fun <T> findAll(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): List<T>

}