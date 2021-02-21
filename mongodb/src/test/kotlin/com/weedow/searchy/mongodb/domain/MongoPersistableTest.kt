package com.weedow.searchy.mongodb.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream

// Not much to test, but exercise to prevent code coverage tool from showing red
internal class MongoPersistableTest {

    @ParameterizedTest
    @MethodSource("mongo_persistable")
    fun test_mongo_persistable(
        mongoPersistable: MongoPersistable<*>,
        idClass: Class<*>,
        createdOn: LocalDateTime?,
        updatedOn: LocalDateTime?,
        isNew: Boolean
    ) {
        assertThat(mongoPersistable.idClass).isEqualTo(idClass)
        assertThat(mongoPersistable.getCreatedOn()).isNull()
        assertThat(mongoPersistable.getUpdatedOn()).isNull()
        assertThat(mongoPersistable.isNew).isTrue
    }

    @Test
    fun test_mongo_persistable_equality() {
        val mongoPersistable1 = StringMongoPersistable()
        val mongoPersistable2 = LongMongoPersistable()

        val mongoPersistable3 = StringMongoPersistable()
        val mongoPersistable4 = LongMongoPersistable()

        // Same ids to null
        assertEqualsMongoPersistable(mongoPersistable1, mongoPersistable3, true)
        assertEqualsMongoPersistable(mongoPersistable2, mongoPersistable4, true)

        mongoPersistable1.setId("123456")
        mongoPersistable2.setId(123456L)

        // Different ids: one has a value, the other is null
        assertNotEqualsMongoPersistable(mongoPersistable1, mongoPersistable3, false)
        assertNotEqualsMongoPersistable(mongoPersistable2, mongoPersistable4, false)

        mongoPersistable3.setId("654321")
        mongoPersistable4.setId(654321L)

        // Different ids: one has a value, the other has a different value
        assertNotEqualsMongoPersistable(mongoPersistable1, mongoPersistable3, false)
        assertNotEqualsMongoPersistable(mongoPersistable2, mongoPersistable4, false)

        mongoPersistable3.setId("123456")
        mongoPersistable4.setId(123456L)

        // Same ids: both have the same value
        assertEqualsMongoPersistable(mongoPersistable1, mongoPersistable3, false)
        assertEqualsMongoPersistable(mongoPersistable2, mongoPersistable4, false)
    }

    private fun assertEqualsMongoPersistable(
        mongoPersistable: MongoPersistable<*>,
        otherMongoPersistable: MongoPersistable<*>,
        isNew: Boolean
    ) {
        assertThat(mongoPersistable.isNew).isEqualTo(isNew)
        assertThat(mongoPersistable).hasSameHashCodeAs(otherMongoPersistable)
        assertThat(mongoPersistable).isEqualTo(otherMongoPersistable)
    }

    private fun assertNotEqualsMongoPersistable(
        mongoPersistable: MongoPersistable<*>,
        otherMongoPersistable: MongoPersistable<*>,
        isNew: Boolean
    ) {
        assertThat(mongoPersistable.isNew).isEqualTo(isNew)
        assertThat(mongoPersistable.hashCode()).isNotEqualTo(otherMongoPersistable.hashCode())
        assertThat(mongoPersistable).isNotEqualTo(otherMongoPersistable)
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun mongo_persistable(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(StringMongoPersistable(), String::class.javaObjectType, null, null, false),
                Arguments.of(LongMongoPersistable(), Long::class.javaObjectType, null, null, false)
            )
        }
    }

    internal class StringMongoPersistable : MongoPersistable<String>()
    internal class LongMongoPersistable : MongoPersistable<Long>()
}