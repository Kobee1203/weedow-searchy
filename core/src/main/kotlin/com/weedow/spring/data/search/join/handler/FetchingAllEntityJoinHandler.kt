package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo
import javax.persistence.criteria.JoinType

class FetchingAllEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean {
        return true
    }

    override fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo {
        return JoinInfo(JoinType.LEFT, true)
    }

}