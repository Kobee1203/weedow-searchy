package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo

class DefaultEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean {
        return true
    }

    override fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo {
        return JoinInfo()
    }

}