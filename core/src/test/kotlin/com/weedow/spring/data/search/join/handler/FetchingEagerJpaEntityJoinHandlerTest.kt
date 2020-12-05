package com.weedow.spring.data.search.join.handler

import com.nhaarman.mockitokotlin2.mock
import com.querydsl.core.JoinType
import com.weedow.spring.data.search.TestDataSearchContext
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.join.JoinInfo
import com.weedow.spring.data.search.querydsl.querytype.PropertyInfos
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.*

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") // Remove warnings for the use of java.lang.annotation.Annotation
@ExtendWith(MockitoExtension::class)
internal class FetchingEagerJpaEntityJoinHandlerTest {

    private fun mockDataSearchContext(): DataSearchContext {
        return mock {
            on { this.joinAnnotations }.thenReturn(TestDataSearchContext.JOIN_ANNOTATIONS)
        }
    }

    @Test
    fun <T> supports_when_field_has_OneToOne_annotation_with_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<OneToOne> {
            on { this.fetch }.thenReturn(FetchType.EAGER)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(OneToOne::class.java)
        }

        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_has_OneToMany_annotation_with_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<OneToMany> {
            on { this.fetch }.thenReturn(FetchType.EAGER)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(OneToMany::class.java)
        }

        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_has_ManyToMany_annotation_with_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<ManyToMany> {
            on { this.fetch }.thenReturn(FetchType.EAGER)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(ManyToMany::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_has_ElementCollection_annotation_with_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<ElementCollection> {
            on { this.fetch }.thenReturn(FetchType.EAGER)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(ElementCollection::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> supports_when_field_ManyToOne_join_annotation_with_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<ManyToOne> {
            on { this.fetch }.thenReturn(FetchType.EAGER)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(ManyToOne::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))

        assertThat(supports).isTrue()
    }

    @Test
    fun <T> dont_supports_when_field_has_OneToOne_annotation_without_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<OneToOne> {
            on { this.fetch }.thenReturn(FetchType.LAZY)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(OneToOne::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_OneToMany_annotation_without_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<OneToMany> {
            on { this.fetch }.thenReturn(FetchType.LAZY)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(OneToMany::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_ManyToMany_annotation_without_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<ManyToMany> {
            on { this.fetch }.thenReturn(FetchType.LAZY)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(ManyToMany::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_ElementCollection_annotation_without_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<ElementCollection> {
            on { this.fetch }.thenReturn(FetchType.LAZY)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(ElementCollection::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> dont_supports_when_field_has_ManyToOne_annotation_without_eager_fetch_type() {
        val dataSearchContext = mockDataSearchContext()

        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(dataSearchContext)

        val joinAnnotation = mock<ManyToOne> {
            on { this.fetch }.thenReturn(FetchType.LAZY)
            on { (this as java.lang.annotation.Annotation).annotationType() }.thenReturn(ManyToOne::class.java)
        }
        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))
        assertThat(supports).isFalse()
    }

    @Test
    fun <T> handle_entity_join_info() {
        val entityJoinHandler = FetchingEagerJpaEntityJoinHandler<T>(mock())

        val joinInfo = entityJoinHandler.handle(mock())

        assertThat(joinInfo).isEqualTo(JoinInfo(JoinType.LEFTJOIN, true))
    }

    private fun mockPropertyInfos(joinAnnotation: Annotation): PropertyInfos {
        val propertyInfos: PropertyInfos = mock {
            on { this.annotations }.thenReturn(listOf(joinAnnotation))
        }
        return propertyInfos
    }
}
