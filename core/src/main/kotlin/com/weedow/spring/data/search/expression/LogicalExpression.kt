package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import org.springframework.data.jpa.domain.Specification

internal data class LogicalExpression(
        private val logicalOperator: LogicalOperator,
        private val expressions: Array<out Expression>
) : Expression {

    override fun <T> toSpecification(entityJoins: EntityJoins): Specification<T> {
        var lastSpecification = Specification.where<T>(null)!!
        expressions.forEach { expression ->
            val specification = expression.toSpecification<T>(entityJoins)
            lastSpecification = if (logicalOperator == LogicalOperator.OR) lastSpecification.or(specification)!! else lastSpecification.and(specification)!!
        }
        return Specification.where(lastSpecification)!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogicalExpression

        if (logicalOperator != other.logicalOperator) return false
        if (!expressions.contentEquals(other.expressions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = logicalOperator.hashCode()
        result = 31 * result + expressions.contentHashCode()
        return result
    }

}
