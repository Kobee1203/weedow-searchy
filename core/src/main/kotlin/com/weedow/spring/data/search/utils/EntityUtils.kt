package com.weedow.spring.data.search.utils

import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

/**
 * Entity utility methods.
 */
object EntityUtils {

    /**
     * Gets the first found [Field] of the given class and its parents (if any) that is annotated with the given annotation.
     *
     * @param clazz the [Class] to query
     * @param annotationClass – the [Annotation] that must be present on a field to be matched
     * @return the [Field] object or `null` if not found
     */
    fun getFieldWithAnnotation(clazz: Class<*>, annotationClass: Class<out Annotation>): Field? {
        val fields = FieldUtils.getFieldsWithAnnotation(clazz, annotationClass)
        return if (fields.isNotEmpty()) fields[0] else null
    }

    /**
     * Gets the [parameterized types][ParameterizedType] of the given field.
     *
     * May return an empty list if the field type is not parameterized
     *
     * @param field The [Field] to query
     * @return List of classes representing the actual type arguments to the Field
     */
    fun getParameterizedTypes(field: Field): List<Class<*>> {
        val type = field.type
        val genericType = field.genericType
        return when {
            type.isArray -> listOf(type.componentType)
            genericType is ParameterizedType -> genericType.actualTypeArguments.map { it as Class<*> }
            else -> listOf()
        }
    }

}