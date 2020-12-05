package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.dsl.SimpleExpression

data class PropertyInfos(
        val parentClass: Class<*>,
        val fieldName: String,
        val elementType: ElementType,
        val type: Class<*>,
        val parametrizedTypes: List<Class<*>>,
        val annotations: List<Annotation>,
        val queryType: Class<out SimpleExpression<*>>,
)