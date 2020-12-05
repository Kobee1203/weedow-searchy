package com.weedow.spring.data.search.context

import com.weedow.spring.data.search.querydsl.querytype.QEntity

interface ConfigurableDataSearchContext : DataSearchContext {
    fun <E> add(entityClass: Class<E>): QEntity<E>
}