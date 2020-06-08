package com.weedow.spring.data.search.join.handler

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.join.JoinInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.*
import javax.persistence.criteria.JoinType

@ExtendWith(MockitoExtension::class)
internal class FetchingEagerEntityJoinHandlerTest {

    @Test
    fun <T> supports_when_field_has_OneToOne_annotation_with_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<OneToOne>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.EAGER)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_has_OneToMany_annotation_with_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<OneToMany>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.EAGER)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_has_ManyToMany_annotation_with_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<ManyToMany>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.EAGER)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_has_ElementCollection_annotation_with_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<ElementCollection>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.EAGER)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_ManyToOne_join_annotation_with_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<ManyToOne>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.EAGER)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> dont_supports_when_field_has_OneToOne_annotation_without_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<OneToOne>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.LAZY)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_OneToMany_annotation_without_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<OneToMany>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.LAZY)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_ManyToMany_annotation_without_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<ManyToMany>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.LAZY)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_ElementCollection_annotation_without_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<ElementCollection>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.LAZY)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_ManyToOne_annotation_without_eager_fetch_type() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinAnnotation = mock<ManyToOne>()
        whenever(joinAnnotation.fetch).thenReturn(FetchType.LAZY)
        val supports = entityJoinHandler.supports(Any::class.java, Any::class.java, "myfield", joinAnnotation)
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> handle_entity_join_info() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val joinInfo = entityJoinHandler.handle(Any::class.java, Any::class.java, "myfield", mock())

        assertThat(joinInfo).isEqualTo(JoinInfo(JoinType.LEFT, true))
    }

}