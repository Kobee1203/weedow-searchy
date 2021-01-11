package com.weedow.searchy.jpa.join.handler

import com.querydsl.core.JoinType
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.join.JoinInfo
import com.weedow.searchy.join.handler.EntityJoinHandler
import com.weedow.searchy.query.querytype.PropertyInfos
import javax.persistence.*

/**
 * [EntityJoinHandler] implementation to fetch all fields (entity fields and nested fields included) with Join Annotation defining [FetchType.EAGER].
 *
 * Technically, it creates a `LEFT JOIN FETCH` in JPQL for Join Annotation with [FetchType.EAGER].
 *
 * _Example: `A` has a relationship with `B` using `@OneToMany` annotation and `FetchType.EAGER`, and `A` has a relationship with `C`
 * using `@OneToMany` annotation and `FetchType.LAZY`. When we search for `A`, we retrieve `A` with just data from `B`, but not `C`._
 *
 * @param searchyContext [SearchyContext]
 */
class JpaFetchingEagerEntityJoinHandler(
    private val searchyContext: SearchyContext
) : EntityJoinHandler {

    override fun supports(propertyInfos: PropertyInfos): Boolean {
        val joinAnnotation = propertyInfos.annotations.firstOrNull { searchyContext.isJoinAnnotation(it.annotationClass.java) }
        val fetchType = getFetchType(joinAnnotation)
        return FetchType.EAGER == fetchType
    }

    override fun handle(propertyInfos: PropertyInfos): JoinInfo {
        return JoinInfo(JoinType.LEFTJOIN, true)
    }

    private fun getFetchType(joinAnnotation: Annotation?): FetchType {
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