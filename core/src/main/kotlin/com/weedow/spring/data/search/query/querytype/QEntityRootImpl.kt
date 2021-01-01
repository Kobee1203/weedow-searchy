package com.weedow.spring.data.search.query.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.Visitor
import java.lang.reflect.AnnotatedElement

/**
 * Default [QEntityRoot] implementation.
 *
 * @param qEntity [QEntity]
 * @param T Entity Class
 */
class QEntityRootImpl<T>(
    private val qEntity: QEntity<out T>
) : QEntityRoot<T> {

    override fun get(fieldName: String): QPath<*> = qEntity.get(fieldName)

    override fun <R : Any?, C : Any?> accept(v: Visitor<R, C>?, context: C?): R? = qEntity.accept(v, context)

    override fun getType(): Class<out T> = qEntity.type

    override fun getMetadata(property: Path<*>?): Any = qEntity.getMetadata(property)

    override fun getMetadata(): PathMetadata = qEntity.metadata

    override fun getRoot(): Path<*> = qEntity.root

    override fun getAnnotatedElement(): AnnotatedElement = qEntity.annotatedElement

}