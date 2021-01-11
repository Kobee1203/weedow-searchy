package com.weedow.searchy.query.specification

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.expression.RootExpression
import com.weedow.searchy.join.EntityJoinManager
import com.weedow.searchy.utils.klogger

/**
 * Default [SpecificationService] implementation.
 *
 * Converts the [RootExpression], containing the [Expressions][com.weedow.searchy.expression.Expression], to a [Specification] object.
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
        if (log.isDebugEnabled) log.debug("Initialized SpecificationService: {}", this::class.qualifiedName)
    }

    override fun <T> createSpecification(rootExpression: RootExpression<T>, searchyDescriptor: SearchyDescriptor<T>): Specification<T> {
        if (log.isDebugEnabled) log.debug("Creating specifications for the following expression: {}", rootExpression)

        val entityJoins = entityJoinManager.computeEntityJoins(searchyDescriptor)

        return rootExpression.toSpecification(entityJoins)
    }

}