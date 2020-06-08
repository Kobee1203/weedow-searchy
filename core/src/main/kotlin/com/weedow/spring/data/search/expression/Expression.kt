package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.join.EntityJoins
import org.springframework.data.jpa.domain.Specification

interface Expression {

    fun <T> toSpecification(entityJoins: EntityJoins): Specification<T>

}
