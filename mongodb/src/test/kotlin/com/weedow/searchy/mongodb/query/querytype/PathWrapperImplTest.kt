package com.weedow.searchy.mongodb.query.querytype

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.Visitor
import com.weedow.searchy.query.querytype.ElementType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.lang.reflect.AnnotatedElement

@ExtendWith(MockitoExtension::class)
internal class PathWrapperImplTest {

    @Test
    fun test() {
        val type = String::class.java
        val metadata = mock<PathMetadata>()
        val root = mock<Path<*>>()
        val annotatedElement = mock<AnnotatedElement>()
        val path = mock<Path<String>> {
            on { this.type }.thenReturn(type)
            on { this.metadata }.thenReturn(metadata)
            on { this.root }.thenReturn(root)
            on { this.annotatedElement }.thenReturn(annotatedElement)
        }
        val pathWrapper = PathWrapperImpl(path, ElementType.MAP_VALUE)

        assertThat(pathWrapper.type).isEqualTo(type)
        assertThat(pathWrapper.metadata).isEqualTo(metadata)
        assertThat(pathWrapper.root).isEqualTo(root)
        assertThat(pathWrapper.annotatedElement).isEqualTo(annotatedElement)
        assertThat(pathWrapper.elementType).isEqualTo(ElementType.MAP_VALUE)

        val visitor = mock<Visitor<Any, Any>>()
        val context = mock<Any>()
        pathWrapper.accept(visitor, context)
        verify(path).accept(visitor, context)
    }
}