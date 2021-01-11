package com.weedow.searchy.descriptor

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.dto.DefaultDtoMapper
import com.weedow.searchy.join.handler.DefaultEntityJoinHandler
import com.weedow.searchy.query.specification.SpecificationExecutor
import com.weedow.searchy.validation.SearchyValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class SearchyDescriptorBuilderTest {

    @Test
    fun build_SearchyDescriptor_with_default_values() {
        val entityClass = Person::class.java

        val searchyDescriptor1 = SearchyDescriptorBuilder.builder<Person>()
            .build()

        assertThat(searchyDescriptor1.id).isEqualTo("person")
        assertThat(searchyDescriptor1.entityClass).isEqualTo(entityClass)
        assertThat(searchyDescriptor1.validators).isEmpty()
        assertThat(searchyDescriptor1.dtoMapper).isNull()
        assertThat(searchyDescriptor1.specificationExecutor).isNull()
        assertThat(searchyDescriptor1.entityJoinHandlers).isEmpty()

        val searchyDescriptor2 = SearchyDescriptorBuilder(entityClass)
            .build()

        assertThat(searchyDescriptor2.id).isEqualTo("person")
        assertThat(searchyDescriptor2.entityClass).isEqualTo(entityClass)
        assertThat(searchyDescriptor2.validators).isEmpty()
        assertThat(searchyDescriptor2.dtoMapper).isNull()
        assertThat(searchyDescriptor1.specificationExecutor).isNull()
        assertThat(searchyDescriptor2.entityJoinHandlers).isEmpty()

        assertThat(searchyDescriptor1).isEqualTo(searchyDescriptor2)
        assertThat(searchyDescriptor1).isNotSameAs(searchyDescriptor2)
    }

    @Test
    fun build_SearchyDescriptor_with_custom_values() {
        val entityClass = Person::class.java

        val validator1 = mock<SearchyValidator>()
        val dtoMapper1 = DefaultDtoMapper<Person>()
        val specificationExecutor1 = mock<SpecificationExecutor<Person>>()
        val entityJoinHandler1 = DefaultEntityJoinHandler()
        val searchyDescriptor1 = SearchyDescriptorBuilder.builder<Person>()
            .id("person1")
            .validators(validator1)
            .dtoMapper(dtoMapper1)
            .specificationExecutor(specificationExecutor1)
            .entityJoinHandlers(entityJoinHandler1)
            .build()

        assertThat(searchyDescriptor1.id).isEqualTo("person1")
        assertThat(searchyDescriptor1.entityClass).isEqualTo(entityClass)
        assertThat(searchyDescriptor1.validators).containsExactly(validator1)
        assertThat(searchyDescriptor1.dtoMapper).isEqualTo(dtoMapper1)
        assertThat(searchyDescriptor1.specificationExecutor).isEqualTo(specificationExecutor1)
        assertThat(searchyDescriptor1.entityJoinHandlers).containsExactly(entityJoinHandler1)

        val validator2 = mock<SearchyValidator>()
        val dtoMapper2 = DefaultDtoMapper<Person>()
        val specificationExecutor2 = mock<SpecificationExecutor<Person>>()
        val entityJoinHandler2 = DefaultEntityJoinHandler()
        val searchyDescriptor2 = SearchyDescriptorBuilder(entityClass)
            .id("person2")
            .validators(validator2)
            .dtoMapper(dtoMapper2)
            .specificationExecutor(specificationExecutor2)
            .entityJoinHandlers(entityJoinHandler2)
            .build()

        assertThat(searchyDescriptor2.id).isEqualTo("person2")
        assertThat(searchyDescriptor2.entityClass).isEqualTo(entityClass)
        assertThat(searchyDescriptor2.validators).containsExactly(validator2)
        assertThat(searchyDescriptor2.dtoMapper).isEqualTo(dtoMapper2)
        assertThat(searchyDescriptor2.specificationExecutor).isEqualTo(specificationExecutor2)
        assertThat(searchyDescriptor2.entityJoinHandlers).containsExactly(entityJoinHandler2)
    }

}