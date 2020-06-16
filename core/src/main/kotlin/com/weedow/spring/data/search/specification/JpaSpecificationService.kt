package com.weedow.spring.data.search.specification

import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoins
import org.springframework.data.jpa.domain.Specification

/**
 * Service interface to create a [Specification] to be used by the [DataSearchService][com.weedow.spring.data.search.service.DataSearchService].
 */
interface JpaSpecificationService {

    /**
     * Create a new [Specification] from the given [RootExpression] and the [EntityJoins].
     *
     * @param rootExpression [RootExpression] object that contains the [Expressions][com.weedow.spring.data.search.expression.Expression]
     * @param entityJoins EntityJoins object that contains the joins related to the Entity
     * @return a [Specification]
     */
    fun <T> createSpecification(rootExpression: RootExpression<T>, entityJoins: EntityJoins): Specification<T>

}