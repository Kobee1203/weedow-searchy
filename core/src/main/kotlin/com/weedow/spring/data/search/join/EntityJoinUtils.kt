package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.FIELD_PATH_SEPARATOR
import java.lang.reflect.Field

/**
 * Entity Join utility methods.
 */
internal object EntityJoinUtils {

    private const val JOIN_NAME_SEPARATOR = "."

    /**
     * Gets the join name from the given arguments.
     *
     * @param entityClass Class of the Entity
     * @param field field of the Entity
     * @return String representing the join name
     */
    fun getJoinName(entityClass: Class<*>, field: Field): String {
        val fieldClass = EntityUtils.getFieldClass(field)
        return if (EntityUtils.isElementCollection(field)) entityClass.canonicalName + JOIN_NAME_SEPARATOR + field.name else fieldClass.canonicalName
    }

    /**
     * Gets the field path from the given arguments.
     *
     * @param parentPath path of the parent Entity
     * @param fieldName name of the field present in the parent Entity.
     * @return String representing the field path
     */
    fun getFieldPath(parentPath: String, fieldName: String): String {
        return if (parentPath.trim().isNotEmpty()) parentPath + FIELD_PATH_SEPARATOR + fieldName else fieldName
    }

}