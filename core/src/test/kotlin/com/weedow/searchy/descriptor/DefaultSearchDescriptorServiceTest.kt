package com.weedow.searchy.descriptor

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.dto.DefaultDtoMapper
import com.weedow.searchy.dto.DtoMapper
import com.weedow.searchy.query.specification.SpecificationExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class DefaultSearchyDescriptorServiceTest {

    @Test
    fun add_and_find_SearchyDescriptor_successfully() {
        val searchyDescriptorId1 = "person"
        val searchyDescriptor1 = mock<SearchyDescriptor<Person>> {
            on { this.id }.doReturn(searchyDescriptorId1)
        }

        val searchyDescriptorId2 = "person2"
        val specificationExecutor2 = mock<SpecificationExecutor<Person>>()
        val searchyDescriptor2 = object : SearchyDescriptor<Person> {
            override val id = searchyDescriptorId2
            override val entityClass = Person::class.java
            override val dtoMapper: DtoMapper<Person, Person> = DefaultDtoMapper()
            override val specificationExecutor: SpecificationExecutor<Person> = specificationExecutor2
        }

        val searchyDescriptorService = DefaultSearchyDescriptorService()

        searchyDescriptorService.addSearchyDescriptor(searchyDescriptor1)
        searchyDescriptorService.addSearchyDescriptor(searchyDescriptor2)

        val resultSearchyDescriptor1 = searchyDescriptorService.getSearchyDescriptor(searchyDescriptorId1)
        assertThat(resultSearchyDescriptor1).isEqualTo(searchyDescriptor1)

        val resultSearchyDescriptor2 = searchyDescriptorService.getSearchyDescriptor(searchyDescriptorId2)
        assertThat(resultSearchyDescriptor2).isEqualTo(searchyDescriptor2)
        assertThat(resultSearchyDescriptor2?.validators).isEmpty()
        assertThat(resultSearchyDescriptor2?.dtoMapper).isEqualTo(DefaultDtoMapper<Person>())
        assertThat(resultSearchyDescriptor2?.dtoMapper).hasSameHashCodeAs(DefaultDtoMapper<Person>())
        assertThat(resultSearchyDescriptor2?.specificationExecutor).isSameAs(specificationExecutor2)
        assertThat(resultSearchyDescriptor2?.entityJoinHandlers).isEmpty()
    }

    @Test
    fun add_and_find_SearchyDescriptor_unsuccessfully() {
        val searchyDescriptorId = "person"
        val searchyDescriptor = mock<SearchyDescriptor<Person>> {
            on { this.id }.doReturn(searchyDescriptorId)
        }

        val searchyDescriptorService = DefaultSearchyDescriptorService()

        searchyDescriptorService.addSearchyDescriptor(searchyDescriptor)

        val resultSearchyDescriptor = searchyDescriptorService.getSearchyDescriptor("unknown")

        assertThat(resultSearchyDescriptor).isNull()
    }
}