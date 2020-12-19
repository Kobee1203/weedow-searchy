package com.weedow.spring.data.search.specification

import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoins
import com.weedow.spring.data.search.utils.klogger
import org.springframework.data.jpa.domain.Specification

/**
 * Default [JpaSpecificationService] implementation.
 *
 * Converts the [RootExpression], containing the [Expressions][com.weedow.spring.data.search.expression.Expression], to a [Specification] object.
 */
class JpaSpecificationServiceImpl : JpaSpecificationService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized JpaSpecificationService: {}", this::class.qualifiedName)
    }

    override fun <T> createSpecification(rootExpression: RootExpression<T>, entityJoins: EntityJoins): Specification<T> {
        if (log.isDebugEnabled) log.debug("Creating specifications for the following expression: {}", rootExpression)
        return rootExpression.toSpecification(entityJoins)
    }

}