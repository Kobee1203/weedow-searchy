package com.weedow.spring.data.search.querydsl.specification

interface QueryDslSpecificationExecutorFactory {

    fun <T> getQueryDslSpecificationExecutor(domainClass: Class<T>): QueryDslSpecificationExecutor<T>

}