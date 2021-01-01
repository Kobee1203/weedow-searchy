package com.weedow.spring.data.search.query.specification

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.utils.klogger

/**
 * Default [SpecificationService] implementation.
 *
 * Converts the [RootExpression], containing the [Expressions][com.weedow.spring.data.search.expression.Expression], to a [Specification] object.
 *
 * @param entityJoinManager [EntityJoinManager]
 */
class SpecificationServiceImpl(
    private val entityJoinManager: EntityJoinManager
) : SpecificationService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized JpaSpecificationService: {}", this::class.qualifiedName)
    }

    override fun <T> createSpecification(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): Specification<T> {
        if (log.isDebugEnabled) log.debug("Creating specifications for the following expression: {}", rootExpression)

        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        return rootExpression.toSpecification(entityJoins)
    }

}