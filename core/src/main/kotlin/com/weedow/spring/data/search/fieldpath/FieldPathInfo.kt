package com.weedow.spring.data.search.fieldpath

/**
 * Value object with field path information.
 */
data class FieldPathInfo(
        val fieldPath: String,
        val fieldName: String,
        val fieldClass: Class<*>,
        val parentClass: Class<*>
)