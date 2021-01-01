package com.weedow.spring.data.search.query.querytype

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.Visitor
import com.weedow.spring.data.search.common.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.lang.reflect.AnnotatedElement

@ExtendWith(MockitoExtension::class)
internal class QEntityRootImplTest {

    @Mock
    private lateinit var qEntity: QEntity<*>

    @InjectMocks
    private lateinit var qEntityRoot: QEntityRootImpl<*>

    @Test
    fun get() {
        val qPath = mock<QPath<*>>()
        whenever(qEntity.get("myfield")).thenReturn(qPath)

        assertThat(qEntityRoot.get("myfield")).isSameAs(qPath)
    }

    @Test
    fun accept() {
        val visitor = mock<Visitor<Any, Any>>()
        val context = mock<Any>()

        val result = mock<QPath<*>>()
        whenever(qEntity.accept(visitor, context)).thenReturn(result)

        assertThat(qEntityRoot.accept(visitor, context)).isSameAs(result)
    }

    @Test
    fun getType() {
        val result = Person::class.java
        whenever(qEntity.type).thenReturn(result)

        assertThat(qEntityRoot.type).isSameAs(result)
    }

    @Test
    fun getMetadata() {
        val result = mock<PathMetadata>()
        whenever(qEntity.metadata).thenReturn(result)

        assertThat(qEntityRoot.metadata).isSameAs(result)
    }

    @Test
    fun getMetadataWithParameter() {
        val property = mock<Path<*>>()

        val result = mock<PathMetadata>()
        whenever(qEntity.getMetadata(property)).thenReturn(result)

        assertThat(qEntityRoot.getMetadata(property)).isSameAs(result)
    }

    @Test
    fun getRoot() {
        val result = mock<Path<*>>()
        whenever(qEntity.root).thenReturn(result)

        assertThat(qEntityRoot.root).isSameAs(result)
    }

    @Test
    fun getAnnotatedElement() {
        val result = mock<AnnotatedElement>()
        whenever(qEntity.annotatedElement).thenReturn(result)

        assertThat(qEntityRoot.annotatedElement).isSameAs(result)
    }
}