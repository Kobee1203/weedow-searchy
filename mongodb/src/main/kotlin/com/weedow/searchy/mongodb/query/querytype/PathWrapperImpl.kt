package com.weedow.searchy.mongodb.query.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.Visitor
import com.weedow.searchy.query.querytype.ElementType
import java.lang.reflect.AnnotatedElement

class PathWrapperImpl<T>(
    val path: Path<T>,
    override val elementType: ElementType
) : PathWrapper<T> {
    override fun <R : Any?, C : Any?> accept(v: Visitor<R, C>?, context: C?): R? = path.accept(v, context)

    override fun getType(): Class<out T> = path.type

    override fun getMetadata(): PathMetadata = path.metadata

    override fun getRoot(): Path<*> = path.root

    override fun getAnnotatedElement(): AnnotatedElement = path.annotatedElement
}