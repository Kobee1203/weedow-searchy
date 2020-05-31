package com.weedow.spring.data.search.field

import java.lang.reflect.Field

data class FieldInfo(
        val fieldPath: String,
        val parentClass: Class<*>,
        val field: Field,
        val fieldClass: Class<*>
)