package com.weedow.spring.data.search.expression

/**
 * Value object with field information.
 *
 * @param fieldPath Path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
 * @param fieldName Name of the field
 * @param parentClass Class where to find the field
 */
data class FieldInfo(
    val fieldPath: String,
    val fieldName: String,
    val parentClass: Class<*>
)