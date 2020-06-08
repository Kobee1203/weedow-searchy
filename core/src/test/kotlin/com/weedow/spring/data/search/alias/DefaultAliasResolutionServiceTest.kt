package com.weedow.spring.data.search.alias

import com.weedow.spring.data.search.common.model.Person
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.reflect.Field
import java.util.stream.Stream

internal class DefaultAliasResolutionServiceTest {

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun resolve_alias_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("firstName", "firstName"), // No resolution
                    Arguments.of("first_name", "firstName"),
                    Arguments.of("last_name", "lastName"),
                    Arguments.of("addressEntities", "addressEntities"), // No resolution
                    Arguments.of("addresses", "addressEntities"),
                    Arguments.of("address", "addressEntities"),
                    Arguments.of("jobEntity", "jobEntity") // No resolution
            )
        }
    }

    @MethodSource("resolve_alias_parameters")
    @ParameterizedTest
    fun resolve_alias(alias: String, fieldName: String) {
        val aliasResolutionService = DefaultAliasResolutionService()

        val personAliasResolver = PersonAliasResolver()
        aliasResolutionService.addAliasResolver(personAliasResolver)

        val result = aliasResolutionService.resolve(Person::class.java, alias)
        Assertions.assertThat(result).isEqualTo(fieldName)
    }

    @Test
    fun resolve_alias_when_no_alias_resolver() {
        val alias = "any_alias"

        val aliasResolutionService = DefaultAliasResolutionService()

        val fieldName = aliasResolutionService.resolve(Person::class.java, alias)

        Assertions.assertThat(fieldName).isEqualTo(alias)
    }

    private class PersonAliasResolver : AliasResolver {

        override fun supports(entityClass: Class<*>, field: Field): Boolean {
            return entityClass == Person::class.java
        }

        override fun resolve(entityClass: Class<*>, field: Field): List<String> {
            return when (field.name) {
                "firstName" -> listOf("first_name")
                "lastName" -> listOf("last_name")
                "addressEntities" -> listOf("address", "addresses")
                else -> emptyList()
            }
        }
    }
}