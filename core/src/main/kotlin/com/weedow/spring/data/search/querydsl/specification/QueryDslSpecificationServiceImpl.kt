package com.weedow.spring.data.search.querydsl.specification

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.utils.klogger

/**
 * Default [QueryDslSpecificationService] implementation.
 *
 * Converts the [RootExpression], containing the [Expressions][com.weedow.spring.data.search.expression.Expression], to a [QueryDslSpecification] object.
 *
 * @param entityJoinManager [EntityJoinManager]
 */
class QueryDslSpecificationServiceImpl(
    private val entityJoinManager: EntityJoinManager
) : QueryDslSpecificationService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized JpaSpecificationService: {}", this::class.qualifiedName)
    }

    override fun <T> createSpecification(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): QueryDslSpecification<T> {
        if (log.isDebugEnabled) log.debug("Creating specifications for the following expression: {}", rootExpression)

        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        return rootExpression.toQueryDslSpecification(entityJoins)
    }

}