package com.weedow.spring.data.search.join

import javax.persistence.*
import javax.persistence.criteria.JoinType

class FetchingEagerEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(fieldJoinInfo: FieldJoinInfo<T>): Boolean {
        val fetchType = getFetchType(fieldJoinInfo.joinAnnotation)
        return FetchType.EAGER == fetchType
    }

    override fun handle(fieldJoinInfo: FieldJoinInfo<T>): FieldJoin {
        return FieldJoin(fieldJoinInfo, JoinType.LEFT, true)
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