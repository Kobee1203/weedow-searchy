package com.weedow.spring.data.search.field

interface FieldPathResolver {

    fun resolveFieldPath(rootClass: Class<*>, fieldPath: String): FieldPathInfo

}