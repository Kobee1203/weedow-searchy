package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.utils.EntityUtils
import java.lang.reflect.Field

data class FieldJoinInfo<T>(
        val rootClass: Class<T>,
        val entityClass: Class<*>,
        val field: Field
) {
    val fieldClass: Class<*> = EntityUtils.getFieldClass(field)
    var fieldName: String = field.name
    val joinAnnotation: Annotation? = EntityUtils.getJoinAnnotationClass(field)?.let { field.getAnnotation(it) }
    val joinName: String = if (EntityUtils.isElementCollection(field)) fieldName else fieldClass.canonicalName

    fun canHandleJoins(fieldJoins: List<FieldJoin>, block: (FieldJoinInfo<T>) -> Unit) {
        // Ignore joins for a field having the same class as the root class
        if (fieldClass == rootClass) {
            return
        }

        // Ignore joins for a field having an entity already processed
        if (fieldJoins.stream().anyMatch { fieldJoin -> fieldJoin.joinName == joinName }) {
            return
        }

        // Ignore joins for a field without a Join Annotation
        joinAnnotation?.let {
            block(this)
        }
    }
}