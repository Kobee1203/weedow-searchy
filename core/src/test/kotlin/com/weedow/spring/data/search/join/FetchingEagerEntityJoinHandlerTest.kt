package com.weedow.spring.data.search.join

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import javax.persistence.*
import javax.persistence.criteria.JoinType

internal class FetchingEagerEntityJoinHandlerTest {

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun field_with_join_annotation_with_eager_fetch_type_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(EntityEager::class.java, "oneToOneField"),
                    Arguments.of(EntityEager::class.java, "oneToManyField"),
                    Arguments.of(EntityEager::class.java, "manyToManyField"),
                    Arguments.of(EntityEager::class.java, "elementCollectionField"),
                    Arguments.of(EntityEager::class.java, "manyToOneField")
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun field_with_join_annotation_without_eager_fetch_type_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(EntityLazy::class.java, "oneToOneField"),
                    Arguments.of(EntityLazy::class.java, "oneToManyField"),
                    Arguments.of(EntityLazy::class.java, "manyToManyField"),
                    Arguments.of(EntityLazy::class.java, "elementCollectionField"),
                    Arguments.of(EntityLazy::class.java, "manyToOneField")
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun handle_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(EntityEager::class.java, String::class.java, "oneToOneField", String::class.java.canonicalName),
                    Arguments.of(EntityEager::class.java, String::class.java, "oneToManyField", String::class.java.canonicalName),
                    Arguments.of(EntityEager::class.java, String::class.java, "manyToManyField", String::class.java.canonicalName),
                    Arguments.of(EntityEager::class.java, String::class.java, "elementCollectionField", "elementCollectionField"),
                    Arguments.of(EntityEager::class.java, String::class.java, "manyToOneField", String::class.java.canonicalName)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("field_with_join_annotation_with_eager_fetch_type_parameters")
    fun <T> supports_when_field_has_join_annotation_with_eager_fetch_type(clazz: Class<T>, fieldName: String) {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val fieldJoinInfo = FieldJoinInfo(clazz, clazz, clazz.getDeclaredField(fieldName))
        val supports = entityJoinHandler.supports(fieldJoinInfo)

        assertThat(supports).isTrue()
    }

    @ParameterizedTest
    @MethodSource("field_with_join_annotation_without_eager_fetch_type_parameters")
    fun <T> dont_supports_when_field_has_join_annotation_without_eager_fetch_type(clazz: Class<T>, fieldName: String) {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val fieldJoinInfo = FieldJoinInfo(clazz, clazz, clazz.getDeclaredField(fieldName))
        val supports = entityJoinHandler.supports(fieldJoinInfo)
        assertThat(supports).isFalse()
    }

    @Test
    fun dont_supports_when_field_has_not_join_annotation() {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<EntityEager>()

        val fieldJoinInfo = FieldJoinInfo(EntityEager::class.java, EntityEager::class.java, EntityEager::class.java.getDeclaredField("simpleField"))
        val supports = entityJoinHandler.supports(fieldJoinInfo)

        assertThat(supports).isFalse()
    }

    @ParameterizedTest
    @MethodSource("handle_parameters")
    fun <T> handle(clazz: Class<T>, fieldClass: Class<*>, fieldName: String, joinName: String) {
        val entityJoinHandler = FetchingEagerEntityJoinHandler<T>()

        val fieldJoinInfo = FieldJoinInfo(clazz, clazz, clazz.getDeclaredField(fieldName))

        val fieldJoin = entityJoinHandler.handle(fieldJoinInfo)

        assertThat(fieldJoin)
                .extracting("parentClass", "fieldClass", "fieldName", "joinName", "joinType", "fetched")
                .containsExactly(clazz, fieldClass, fieldName, joinName, JoinType.LEFT, true)
    }

    internal class EntityEager(
            @Column
            val simpleField: String,

            @OneToOne(fetch = FetchType.EAGER)
            val oneToOneField: String,

            @OneToMany(fetch = FetchType.EAGER)
            val oneToManyField: String,

            @ManyToMany(fetch = FetchType.EAGER)
            var manyToManyField: String,

            @ElementCollection(fetch = FetchType.EAGER)
            var elementCollectionField: String,

            @ManyToOne(fetch = FetchType.EAGER)
            var manyToOneField: String
    )

    internal class EntityLazy(
            @OneToOne(fetch = FetchType.LAZY)
            val oneToOneField: String,

            @OneToMany(fetch = FetchType.LAZY)
            val oneToManyField: String,

            @ManyToMany(fetch = FetchType.LAZY)
            var manyToManyField: String,

            @ElementCollection(fetch = FetchType.LAZY)
            var elementCollectionField: String,

            @ManyToOne(fetch = FetchType.LAZY)
            var manyToOneField: String
    )
}