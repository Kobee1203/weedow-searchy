package com.weedow.searchy.join

import com.weedow.searchy.utils.FIELD_PATH_SEPARATOR

/**
 * Entity Join utility methods.
 */
internal object EntityJoinUtils {

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