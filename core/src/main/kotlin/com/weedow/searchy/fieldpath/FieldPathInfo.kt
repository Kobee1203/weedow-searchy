package com.weedow.searchy.fieldpath

/**
 * Value object with field path information.
 *
 * @param fieldPath Path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
 * @param fieldName Name of the field
 * @param fieldClass Class of the Field
 * @param parentClass Class where to find the field
 */
data class FieldPathInfo(
    val fieldPath: String,
    val fieldName: String,
    val fieldClass: Class<*>,
    val parentClass: Class<*>
)