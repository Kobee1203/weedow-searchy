package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import org.springframework.data.jpa.domain.Specification

/**
 * Interface to represent a query Expression.
 */
interface Expression {

    /**
     * Converts this Expression to a [Specification].
     *
     * @param entityJoins
     */
    fun <T> toSpecification(entityJoins: EntityJoins): Specification<T>

}
