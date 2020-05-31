package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.FIELD_PATH_SEPARATOR
import java.lang.reflect.Field

internal object EntityJoinUtils {

    private const val JOIN_NAME_SEPARATOR = "."

    fun getJoinName(entityClass: Class<*>, field: Field): String {
        val fieldClass = EntityUtils.getFieldClass(field)
        return if (EntityUtils.isElementCollection(field)) entityClass.canonicalName + JOIN_NAME_SEPARATOR + field.name else fieldClass.canonicalName
    }

    fun getFieldPath(parentPath: String, fieldName: String): String {
        return if (parentPath.trim().isNotEmpty()) parentPath + FIELD_PATH_SEPARATOR + fieldName else fieldName
    }

}