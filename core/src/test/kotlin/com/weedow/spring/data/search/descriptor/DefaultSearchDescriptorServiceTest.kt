package com.weedow.spring.data.search.descriptor

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.common.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@ExtendWith(MockitoExtension::class)
internal class DefaultSearchDescriptorServiceTest {

    @Test
    fun add_and_find_SearchDescriptor_successfully() {
        val searchDescriptorId = "person"
        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.id }.doReturn(searchDescriptorId)
        }

        val searchDescriptorId2 = "person2"
        val searchDescriptor2 = object : SearchDescriptor<Person> {
            override val id = searchDescriptorId2
            override val entityClass = Person::class.java
            override val jpaSpecificationExecutor = mock<JpaSpecificationExecutor<Person>>()
        }

        val searchDescriptorService = DefaultSearchDescriptorService()

        searchDescriptorService.addSearchDescriptor(searchDescriptor)
        searchDescriptorService.addSearchDescriptor(searchDescriptor2)

        val resultSearchDescriptor = searchDescriptorService.getSearchDescriptor(searchDescriptorId)
        assertThat(resultSearchDescriptor).isEqualTo(searchDescriptor)

        val resultSearchDescriptor2 = searchDescriptorService.getSearchDescriptor(searchDescriptorId2)
        assertThat(resultSearchDescriptor2).isEqualTo(searchDescriptor2)
        assertThat(resultSearchDescriptor2?.dtoMapper).isEqualTo(DefaultDtoMapper<Person>())
        assertThat(resultSearchDescriptor2?.dtoMapper).hasSameHashCodeAs(DefaultDtoMapper<Person>())
        assertThat(resultSearchDescriptor2?.entityJoinHandlers).isEmpty()
    }

    @Test
    fun add_and_find_SearchDescriptor_unsuccessfully() {
        val searchDescriptorId = "person"
        val searchDescriptor = mock<SearchDescriptor<Person>> {
            on { this.id }.doReturn(searchDescriptorId)
        }

        val searchDescriptorService = DefaultSearchDescriptorService()

        searchDescriptorService.addSearchDescriptor(searchDescriptor)

        val resultSearchDescriptor = searchDescriptorService.getSearchDescriptor("unknown")

        assertThat(resultSearchDescriptor).isNull()
    }
}