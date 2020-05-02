package com.weedow.spring.data.search.utils

import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import javax.persistence.*

object EntityUtils {
    private val JOIN_ANNOTATIONS = listOf(
            OneToOne::class.java,
            OneToMany::class.java,
            ManyToMany::class.java,
            ElementCollection::class.java,
            ManyToOne::class.java
    )

    fun getFieldsWithAnnotation(clazz: Class<*>, annotationClass: Class<out Annotation>): Array<Field> {
        return FieldUtils.getAllFieldsList(clazz)
                .stream()
                .filter { field: Field -> field.isAnnotationPresent(annotationClass) }
                .toArray<Field> { size -> arrayOfNulls(size) }
    }

    fun getFieldClass(field: Field): Class<*> {
        var fieldClass = field.type
        if (Collection::class.java.isAssignableFrom(fieldClass)) {
            val genericType = field.genericType as ParameterizedType
            fieldClass = genericType.actualTypeArguments[0] as Class<*>
        }
        return fieldClass
    }

    fun getJoinAnnotationClass(field: Field): Class<out Annotation>? {
        return JOIN_ANNOTATIONS.firstOrNull { annotation: Class<out Annotation> -> field.getAnnotation(annotation) != null }
    }

    fun isElementCollection(field: Field): Boolean {
        return field.getAnnotation(ElementCollection::class.java) != null
    }
}