package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo
import javax.persistence.criteria.JoinType

/**
* [EntityJoinHandler] implementation to fetch all fields (entity fields and nested fields included) with any Join Annotation.
 *
 * Technically, it creates a `LEFT JOIN FETCH` in JPQL.
 *
 * _Example: `A` has a relationship with `B` and `B` has a relationship with `C`.
 * When we search for `A`, we retrieve `A` with data from `B` and `C`._
 */
class FetchingAllEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean {
        return true
    }

    override fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo {
        return JoinInfo(JoinType.LEFT, true)
    }

}