package com.weedow.spring.data.search.fieldpath

import com.weedow.spring.data.search.alias.AliasResolutionService
import com.weedow.spring.data.search.utils.EntityUtils
import com.weedow.spring.data.search.utils.FIELD_PATH_SEPARATOR
import com.weedow.spring.data.search.utils.klogger
import java.lang.reflect.Field

/**
 * Default [FieldPathResolver] implementation.
 *
 * The field paths are resolved by splitting the paths to a list of Strings around occurrences of the delimiter [FIELD_PATH_SEPARATOR].
 *
 * An [AliasResolutionService] checks if a part of the field path is an alias. If so, it returns the real field name, otherwise it returns the received part.
 */
class FieldPathResolverImpl(private val aliasResolutionService: AliasResolutionService) : FieldPathResolver {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized FieldPathResolver: {}", this)
    }

    override fun resolveFieldPath(rootClass: Class<*>, fieldPath: String): FieldPathInfo {
        var field: Field? = null
        var parentClass = rootClass
        var fieldClass = rootClass
        var resolvedFieldPath = mutableListOf<String>()
        try {
            val parts = fieldPath.split(FIELD_PATH_SEPARATOR)
            for (fieldPart in parts) {
                parentClass = fieldClass
                val fieldName = aliasResolutionService.resolve(parentClass, fieldPart)
                field = parentClass.getDeclaredField(fieldName)
                fieldClass = EntityUtils.getFieldClass(field)
                resolvedFieldPath.add(fieldName)
            }
        } catch (e: NoSuchFieldException) {
            throw IllegalArgumentException("Could not resolve the field path [$fieldPath] from [$rootClass]: ${e.message}", e)
        }
        return FieldPathInfo(resolvedFieldPath.joinToString(FIELD_PATH_SEPARATOR), parentClass, field!!, fieldClass)
    }

}