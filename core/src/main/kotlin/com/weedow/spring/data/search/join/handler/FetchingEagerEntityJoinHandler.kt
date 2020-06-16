package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo
import javax.persistence.*
import javax.persistence.criteria.JoinType

/**
 * [EntityJoinHandler] implementation to fetch all fields (entity fields and nested fields included) with Join Annotation defining [FetchType.EAGER].
 *
 * Technically, it creates a `LEFT JOIN FETCH` in JPQL for Join Annotation with [FetchType.EAGER].
 *
 * _Example: `A` has a relationship with `B` using `@OneToMany` annotation and `FetchType.EAGER`, and `A` has a relationship with `C` using `@OneToMany` annotation and `FetchType.LAZY`.
 * When we search for `A`, we retrieve `A` with just data from `B`, but not `C`._
 */
class FetchingEagerEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean {
        val fetchType = getFetchType(joinAnnotation)
        return FetchType.EAGER == fetchType
    }

    override fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo {
        return JoinInfo(JoinType.LEFT, true)
    }

    private fun getFetchType(joinAnnotation: Annotation): FetchType {
        return when (joinAnnotation) {
            is OneToOne -> joinAnnotation.fetch
            is OneToMany -> joinAnnotation.fetch
            is ManyToMany -> joinAnnotation.fetch
            is ElementCollection -> joinAnnotation.fetch
            is ManyToOne -> joinAnnotation.fetch
            else -> FetchType.LAZY
        }
    }

}