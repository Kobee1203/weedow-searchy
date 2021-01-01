package com.weedow.spring.data.search.query.specification

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoins

/**
 * Service interface to create a [Specification] to be used by the [EntitySearchService][com.weedow.spring.data.search.service.EntitySearchService].
 */
interface SpecificationService {

    /**
     * Create a new [Specification] from the given [RootExpression] and the [EntityJoins].
     *
     * @param rootExpression [RootExpression] object that contains the [Expressions][com.weedow.spring.data.search.expression.Expression]
     * @param searchDescriptor [SearchDescriptor] object that contains the configuration for a specific Entity Class
     * @return a [Specification]
     */
    fun <T> createSpecification(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): Specification<T>

}