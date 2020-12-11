package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.EntityPath

interface QEntity<T> : EntityPath<T> {

    fun get(fieldName: String): QPath<*>

}
