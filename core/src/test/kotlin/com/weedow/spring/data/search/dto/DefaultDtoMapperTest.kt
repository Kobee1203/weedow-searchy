package com.weedow.spring.data.search.dto

import com.weedow.spring.data.search.example.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultDtoMapperTest {

    @Test
    fun map_entity_to_dto() {
        val dtoMapper = DefaultDtoMapper<Person>()

        val source = Person("John", "Doe")
        val result = dtoMapper.map(source)

        assertThat(result).isSameAs(source)
    }

}