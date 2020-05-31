package com.weedow.spring.data.search.specification

import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoins
import org.springframework.data.jpa.domain.Specification

interface JpaSpecificationService {

    fun <T> createSpecification(rootExpression: RootExpression<T>, entityJoins: EntityJoins): Specification<T>

}