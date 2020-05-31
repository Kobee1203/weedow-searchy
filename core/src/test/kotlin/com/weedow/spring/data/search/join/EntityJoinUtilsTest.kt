package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.example.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.stream.Stream

internal class EntityJoinUtilsTest {

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun get_join_name(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(Person::class.java, "jobEntity", Job::class.java.canonicalName),
                    Arguments.of(Person::class.java, "addressEntities", Address::class.java.canonicalName),
                    Arguments.of(Person::class.java, "vehicles", Vehicle::class.java.canonicalName),
                    Arguments.of(Person::class.java, "nickNames", Person::class.java.canonicalName + "." + "nickNames"), // ElementCollection

                    // Test fields without join annotation, no check in the method
                    Arguments.of(Person::class.java, "firstName", String::class.java.canonicalName),
                    Arguments.of(Person::class.java, "birthday", LocalDateTime::class.java.canonicalName),
                    Arguments.of(Person::class.java, "height", "java.lang.Double"), // Double::class.java returns "double"
                    Arguments.of(Vehicle::class.java, "vehicleType", VehicleType::class.java.canonicalName)
            )
        }
    }

    @ParameterizedTest
    @MethodSource("get_join_name")
    fun get_join_name(entityClass: Class<*>, fieldName: String, expectedJoinName: String) {
        val field = entityClass.getDeclaredField(fieldName)
        val joinName = EntityJoinUtils.getJoinName(entityClass, field)

        assertThat(joinName).isEqualTo(expectedJoinName)
    }

    @Test
    fun get_field_path_with_parent_path() {
        val parentPath = "parentPath"
        val fieldName = "fieldName"
        val fieldPath = EntityJoinUtils.getFieldPath(parentPath, fieldName)

        assertThat(fieldPath).isEqualTo("$parentPath.$fieldName")
    }

    @Test
    fun get_field_path_without_parent_path() {
        val fieldName1 = "fieldName"
        val fieldPath1 = EntityJoinUtils.getFieldPath("", fieldName1)

        assertThat(fieldPath1).isEqualTo(fieldName1)

        val fieldName2 = "fieldName"
        val fieldPath2 = EntityJoinUtils.getFieldPath(" ", fieldName2)

        assertThat(fieldPath2).isEqualTo(fieldName2)
    }

}