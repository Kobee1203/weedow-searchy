package com.weedow.searchy.dto

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.searchy.common.dto.PersonDto
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.descriptor.SearchyDescriptor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class DefaultDtoConverterServiceImplTest {

    @Mock
    private lateinit var defaultDtoMapper: DtoMapper<Person, *>

    @InjectMocks
    private lateinit var dtoConverterService: DefaultDtoConverterServiceImpl<Person, *>

    @Test
    fun convert_with_default_dto_mapper() {
        val person1 = Person("John", "Doe", "john.doe@acme.com")
        val person2 = Person("Jane", "Doe", "jane.doe@acme.com")
        val entities = listOf(person1, person2)

        val searchyDescriptor: SearchyDescriptor<Person> = mock {
            on { this.dtoMapper }.thenReturn(null)
        }

        val personDto1 = PersonDto.Builder().firstName("John").lastName("Doe").email("john.doe@acme.com")
        val personDto2 = PersonDto.Builder().firstName("Jane").lastName("Doe").email("jane.doe@acme.com")
        whenever(defaultDtoMapper.map(person1)).thenReturn(personDto1)
        whenever(defaultDtoMapper.map(person2)).thenReturn(personDto2)

        val result = dtoConverterService.convert(entities, searchyDescriptor)

        assertThat(result).hasSize(2)
        assertThat(result).containsExactly(personDto1, personDto2)
    }

    @Test
    fun convert_with_custom_dto_mapper() {
        val person1 = Person("John", "Doe", "john.doe@acme.com")
        val person2 = Person("Jane", "Doe", "jane.doe@acme.com")
        val entities = listOf(person1, person2)

        val customDtoMapper = mock<DtoMapper<Person, *>>()
        val searchyDescriptor: SearchyDescriptor<Person> = mock {
            on { this.dtoMapper }.thenReturn(customDtoMapper)
        }

        val personDto1 = PersonDto.Builder().firstName("John").lastName("Doe").email("john.doe@acme.com")
        val personDto2 = PersonDto.Builder().firstName("Jane").lastName("Doe").email("jane.doe@acme.com")
        whenever(customDtoMapper.map(person1)).thenReturn(personDto1)
        whenever(customDtoMapper.map(person2)).thenReturn(personDto2)

        val result = dtoConverterService.convert(entities, searchyDescriptor)

        assertThat(result).hasSize(2)
        assertThat(result).containsExactly(personDto1, personDto2)

        verifyZeroInteractions(defaultDtoMapper)
    }
}