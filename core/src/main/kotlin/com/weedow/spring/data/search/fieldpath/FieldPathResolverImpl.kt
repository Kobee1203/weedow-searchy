package com.weedow.spring.data.search.fieldpath

import com.weedow.spring.data.search.alias.AliasResolutionService
import com.weedow.spring.data.search.utils.*
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
        var fieldName: String = fieldPath
        var fieldClass = rootClass
        var parentClass = rootClass
        var resolvedFieldPath = mutableListOf<String>()
        try {
            var field: Field? = null
            val parts = fieldPath.split(FIELD_PATH_SEPARATOR)
            for (fieldPart in parts) {
                parentClass = fieldClass
                fieldName = aliasResolutionService.resolve(parentClass, fieldPart)
                fieldClass =
                        if (Map::class.java.isAssignableFrom(parentClass)) {
                            val genericTypes = EntityUtils.getGenericTypes(field!!)
                            when (fieldName) {
                                MAP_KEY -> genericTypes[0]
                                MAP_VALUE -> genericTypes[1]
                                else -> throw IllegalArgumentException("Invalid field path: $fieldPath. The part '$fieldName' is not authorized for a parent field of type Map")
                            }
                        } else {
                            field = parentClass.getDeclaredField(fieldName)
                            EntityUtils.getFieldClass(field)
                        }
                resolvedFieldPath.add(fieldName)
            }
        } catch (e: NoSuchFieldException) {
            throw IllegalArgumentException("Could not resolve the field path [$fieldPath] from [$rootClass]: ${e.message}", e)
        }
        return FieldPathInfo(resolvedFieldPath.joinToString(FIELD_PATH_SEPARATOR), fieldName, fieldClass, parentClass)
    }

}