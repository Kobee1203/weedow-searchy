package com.weedow.spring.data.search.utils

import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import javax.persistence.*

/**
 * Entity utility methods.
 */
object EntityUtils {
    /**
     * List of Join Annotations supported.
     */
    private val JOIN_ANNOTATIONS = listOf(
            OneToOne::class.java,
            OneToMany::class.java,
            ManyToMany::class.java,
            ElementCollection::class.java,
            ManyToOne::class.java
    )

    /**
     * Gets an array of [Fields][Field] of the given [Class][clazz], annotated with the given [annotation Class][annotationClass].
     *
     * @param clazz Class where the fields are found
     * @param annotationClass: Annotation Class present on any field
     * @return an Array of [Fields][Field].
     */
    fun getFieldsWithAnnotation(clazz: Class<*>, annotationClass: Class<out Annotation>): Array<Field> {
        return FieldUtils.getAllFieldsList(clazz)
                .stream()
                .filter { field: Field -> field.isAnnotationPresent(annotationClass) }
                .toArray<Field> { size -> arrayOfNulls(size) }
    }

    /**
     * Gets the type of the given [field].
     *
     * @param field [Field] object
     * @return Class representing the type of the field.
     */
    fun getFieldClass(field: Field): Class<*> {
        var fieldClass = field.type
        if (Collection::class.java.isAssignableFrom(fieldClass)) {
            fieldClass = getGenericTypes(field)[0]
        }
        return fieldClass
    }

    /**
     * Gets the generic types of the given field.
     */
    fun getGenericTypes(field: Field): List<Class<*>> {
        val genericType = field.genericType as ParameterizedType
        return genericType.actualTypeArguments.map { it as Class<*> }
    }

    /**
     * Gets the [Join Annotation][JOIN_ANNOTATIONS] of the given [field].
     *
     * If the field has no [Join Annotation][JOIN_ANNOTATIONS], the methods returns `null`.
     *
     * @param field [Field] object
     * @return Class that extends [Annotation] present on the field, or `null` if no [Join Annotation][JOIN_ANNOTATIONS].
     */
    fun getJoinAnnotationClass(field: Field): Class<out Annotation>? {
        return JOIN_ANNOTATIONS.firstOrNull { annotation: Class<out Annotation> -> field.getAnnotation(annotation) != null }
    }

    /**
     * Checks if the given [field] is annotated with [ElementCollection].
     *
     * @param field [Field] object
     * @return `true` if the [field] is annotated with [ElementCollection], `false` instead.
     */
    fun isElementCollection(field: Field): Boolean {
        return field.getAnnotation(ElementCollection::class.java) != null
    }
}