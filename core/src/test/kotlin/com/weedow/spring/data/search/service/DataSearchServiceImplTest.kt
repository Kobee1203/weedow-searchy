package com.weedow.spring.data.search.service

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.join.DefaultEntityJoinHandler
import com.weedow.spring.data.search.join.EntityJoinHandler
import com.weedow.spring.data.search.specification.JpaSpecificationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

@ExtendWith(MockitoExtension::class)
internal class DataSearchServiceImplTest {

    @Mock
    lateinit var jpaSpecificationService: JpaSpecificationService

    @InjectMocks
    lateinit var dataSearchService: DataSearchServiceImpl

    @Test
    fun findAll() {
        val fieldInfos = listOf(
                FieldInfo("firstName", Person::class.java, Person::class.java.getDeclaredField("firstName"), String::class.java, listOf("John"))
        )

        val entityJoinHandler = mock<EntityJoinHandler<Person>>()

        val specification = mock<Specification<Person>>()
        whenever(jpaSpecificationService.createSpecification(fieldInfos, Person::class.java, mutableListOf(entityJoinHandler, DefaultEntityJoinHandler())))
                .thenReturn(specification)

        val person1 = Person("John", "Doe")

        val jpaSpecificationExecutor = mock<JpaSpecificationExecutor<Person>>()
        whenever(jpaSpecificationExecutor.findAll(specification))
                .thenReturn(listOf(person1))

        val searchDescriptor = SearchDescriptorBuilder.builder<Person>()
                .entityJoinHandlers(entityJoinHandler)
                .jpaSpecificationExecutor(jpaSpecificationExecutor)
                .build()

        val result = dataSearchService.findAll(fieldInfos, searchDescriptor)

        assertThat(result).containsExactly(person1)
    }
}