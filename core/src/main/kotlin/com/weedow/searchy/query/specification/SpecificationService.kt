package com.weedow.searchy.query.specification

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.expression.RootExpression
import com.weedow.searchy.join.EntityJoins

/**
 * Service interface to create a [Specification] to be used by the [EntitySearchService][com.weedow.searchy.service.EntitySearchService].
 */
interface SpecificationService {

    /**
     * Create a new [Specification] from the given [RootExpression] and the [EntityJoins].
     *
     * @param rootExpression [RootExpression] object that contains the [Expressions][com.weedow.searchy.expression.Expression]
     * @param searchyDescriptor [SearchyDescriptor] object that contains the configuration for a specific Entity Class
     * @return a [Specification]
     */
    fun <T> createSpecification(rootExpression: RootExpression<T>, searchyDescriptor: SearchyDescriptor<T>): Specification<T>

}