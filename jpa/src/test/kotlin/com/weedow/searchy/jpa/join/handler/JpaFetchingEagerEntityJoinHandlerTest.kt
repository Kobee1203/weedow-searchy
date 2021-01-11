package com.weedow.searchy.jpa.join.handler

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.querydsl.core.JoinType
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.join.JoinInfo
import com.weedow.searchy.query.querytype.PropertyInfos
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.junit.jupiter.MockitoExtension
import java.util.stream.Stream
import javax.persistence.*
import kotlin.reflect.KClass

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") // Remove warnings for the use of java.lang.annotation.Annotation
@ExtendWith(MockitoExtension::class)
internal class JpaFetchingEagerEntityJoinHandlerTest {

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun get_join_annotation_with_eager_fetch_type(): Stream<Arguments> {
            return get_join_annotation(FetchType.EAGER)
        }

        @JvmStatic
        @Suppress("unused")
        private fun get_join_annotation_with_lazy_fetch_type(): Stream<Arguments> {
            return get_join_annotation(FetchType.LAZY)
        }

        private fun get_join_annotation(fetchType: FetchType): Stream<Arguments> {
            return Stream.of(
                Arguments.of(OneToOne::class, mock<OneToOne> { on { this.fetch }.thenReturn(fetchType) }),
                Arguments.of(OneToMany::class, mock<OneToMany> { on { this.fetch }.thenReturn(fetchType) }),
                Arguments.of(ManyToMany::class, mock<ManyToMany> { on { this.fetch }.thenReturn(fetchType) }),
                Arguments.of(ElementCollection::class, mock<ElementCollection> { on { this.fetch }.thenReturn(fetchType) }),
                Arguments.of(ManyToOne::class, mock<ManyToOne> { on { this.fetch }.thenReturn(fetchType) })
            )
        }
    }

    @ParameterizedTest
    @MethodSource("get_join_annotation_with_eager_fetch_type")
    fun <T : Annotation> supports_when_field_has_join_annotation_with_eager_fetch_type(annotationClass: KClass<T>, joinAnnotation: T) {
        val searchyContext = mock<SearchyContext> {
            on { this.isJoinAnnotation(annotationClass.javaObjectType) }.thenReturn(true)
        }

        val entityJoinHandler = JpaFetchingEagerEntityJoinHandler(searchyContext)

        whenever((joinAnnotation as java.lang.annotation.Annotation).annotationType()).thenReturn(annotationClass.javaObjectType)

        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))

        assertThat(supports).isTrue
    }

    @ParameterizedTest
    @MethodSource("get_join_annotation_with_lazy_fetch_type")
    fun <T : Annotation> dont_support_when_field_has_join_annotation_with_lazy_fetch_type(annotationClass: KClass<T>, joinAnnotation: T) {
        val searchyContext = mock<SearchyContext> {
            on { this.isJoinAnnotation(annotationClass.javaObjectType) }.thenReturn(true)
        }

        val entityJoinHandler = JpaFetchingEagerEntityJoinHandler(searchyContext)

        whenever((joinAnnotation as java.lang.annotation.Annotation).annotationType()).thenReturn(annotationClass.javaObjectType)

        val supports = entityJoinHandler.supports(mockPropertyInfos(joinAnnotation))

        assertThat(supports).isFalse
    }

    @Test
    fun dont_support_when_field_has_no_annotation() {
        val searchyContext = mock<SearchyContext>()

        val entityJoinHandler = JpaFetchingEagerEntityJoinHandler(searchyContext)

        val supports = entityJoinHandler.supports(mockPropertyInfos(null))

        assertThat(supports).isFalse
        verifyZeroInteractions(searchyContext)
    }

    @Test
    fun handle_entity_join_info() {
        val entityJoinHandler = JpaFetchingEagerEntityJoinHandler(mock())

        val joinInfo = entityJoinHandler.handle(mock())

        assertThat(joinInfo).isEqualTo(JoinInfo(JoinType.LEFTJOIN, true))
    }

    private fun mockPropertyInfos(joinAnnotation: Annotation?): PropertyInfos = mock {
        on { annotations }.thenReturn(if (joinAnnotation != null) listOf(joinAnnotation) else emptyList())
    }
}
