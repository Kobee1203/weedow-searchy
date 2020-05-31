package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo

interface EntityJoinHandler<T> {

    fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean

    fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo

}