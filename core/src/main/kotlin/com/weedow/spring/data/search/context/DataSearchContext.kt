package com.weedow.spring.data.search.context

import com.weedow.spring.data.search.querydsl.querytype.PropertyInfos
import com.weedow.spring.data.search.querydsl.querytype.QEntity

interface DataSearchContext {

    val entityAnnotations: List<Class<out Annotation>>

    val joinAnnotations: List<Class<out Annotation>>

    fun <E> get(
        entityClass: Class<E>,
        default: (entityClazz: Class<E>) -> QEntity<E> = { entityClazz -> throw IllegalArgumentException("Could not found the QEntity for $entityClazz") }
    ): QEntity<E>

    fun getAllPropertyInfos(entityClass: Class<*>): List<PropertyInfos>

    fun isEntity(clazz: Class<*>): Boolean

}
