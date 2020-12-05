package com.weedow.spring.data.search.querydsl.specification

interface QueryDslSpecificationExecutor<T> {

    fun findAll(specification: QueryDslSpecification<T>?): List<T>

}