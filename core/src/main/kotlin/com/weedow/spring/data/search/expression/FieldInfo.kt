package com.weedow.spring.data.search.expression

/**
 * Value object with field information.
 */
data class FieldInfo(
        val fieldPath: String,
        val fieldName: String,
        val parentClass: Class<*>
)