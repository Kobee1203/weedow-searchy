package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo
import javax.persistence.*
import javax.persistence.criteria.JoinType

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