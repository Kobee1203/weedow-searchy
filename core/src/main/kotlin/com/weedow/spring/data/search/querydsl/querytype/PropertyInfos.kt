package com.weedow.spring.data.search.querydsl.querytype

import com.querydsl.core.types.dsl.SimpleExpression

/**
 * Data class with property information.
 *
 * @param qName fully qualified name
 * @param parentClass Parent Class of the current property
 * @param fieldName name of the property
 * @param elementType property [element type][ElementType]
 * @param type property type
 * @param parameterizedTypes List of parameterized types. Can be empty when the [property type][type] is not a generic type
 * @param annotations List of annotations specified for the property
 * @param queryType Expression type
 */
data class PropertyInfos(
    val qName: String,
    val parentClass: Class<*>,
    val fieldName: String,
    val elementType: ElementType,
    val type: Class<*>,
    val parameterizedTypes: List<Class<*>>,
    val annotations: List<Annotation>,
    val queryType: Class<out SimpleExpression<*>>
)