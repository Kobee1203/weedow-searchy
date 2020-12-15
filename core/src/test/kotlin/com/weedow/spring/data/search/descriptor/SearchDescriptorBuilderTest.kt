package com.weedow.spring.data.search.descriptor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.config.JpaSpecificationExecutorFactory
import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.example.PersonDtoMapper
import com.weedow.spring.data.search.example.PersonRepositoryImpl
import com.weedow.spring.data.search.join.handler.DefaultEntityJoinHandler
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutor
import com.weedow.spring.data.search.validation.DataSearchValidator
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager
import javax.persistence.metamodel.EntityType
import javax.persistence.metamodel.Metamodel

@ExtendWith(MockitoExtension::class)
internal class SearchDescriptorBuilderTest {

    @Test
    fun build_SearchDescriptor_with_default_values_and_JpaSpecificationExecutorFactory_initialized() {
        val entityClass = Person::class.java

        val entityManager = mockEntityManager(entityClass, true)

        JpaSpecificationExecutorFactory.init(entityManager)
        try {
            val searchDescriptor1 = SearchDescriptorBuilder.builder<Person>()
                .build()

            assertThat(searchDescriptor1.id).isEqualTo("person")
            assertThat(searchDescriptor1.entityClass).isEqualTo(entityClass)
            assertThat(searchDescriptor1.validators).isEmpty()
            assertThat(searchDescriptor1.dtoMapper).isEqualTo(DefaultDtoMapper<Person>())
            assertThat(searchDescriptor1.jpaSpecificationExecutor).isNotNull
            assertThat(searchDescriptor1.jpaSpecificationExecutor).isInstanceOf(SimpleJpaRepository::class.java)
            assertThat(searchDescriptor1.queryDslSpecificationExecutor).isNull()
            assertThat(searchDescriptor1.entityJoinHandlers).isEmpty()

            val searchDescriptor2 = SearchDescriptorBuilder(entityClass)
                .build()

            assertThat(searchDescriptor2.id).isEqualTo("person")
            assertThat(searchDescriptor2.entityClass).isEqualTo(entityClass)
            assertThat(searchDescriptor2.validators).isEmpty()
            assertThat(searchDescriptor2.dtoMapper).isEqualTo(DefaultDtoMapper<Person>())
            assertThat(searchDescriptor2.jpaSpecificationExecutor).isNotNull
            assertThat(searchDescriptor2.jpaSpecificationExecutor).isInstanceOf(SimpleJpaRepository::class.java)
            assertThat(searchDescriptor1.queryDslSpecificationExecutor).isNull()
            assertThat(searchDescriptor2.entityJoinHandlers).isEmpty()

            // TODO Uncomment the following line when JpaSpecificationExecutor will be removed
            // assertThat(searchDescriptor1).isEqualTo(searchDescriptor2)
            assertThat(searchDescriptor1).isNotSameAs(searchDescriptor2)
        } finally {
            JpaSpecificationExecutorFactory.reset()
        }
    }

    @Test
    fun build_SearchDescriptor_with_custom_values() {
        val entityClass = Person::class.java

        val validator1 = mock<DataSearchValidator>()
        val dtoMapper1 = PersonDtoMapper()
        val specificationExecutor1 = PersonRepositoryImpl(entityClass, mockEntityManager(entityClass, false))
        val queryDslSpecificationExecutor1 = mock<QueryDslSpecificationExecutor<Person>>()
        val entityJoinHandler1 = DefaultEntityJoinHandler()
        val searchDescriptor1 = SearchDescriptorBuilder.builder<Person>()
            .id("person1")
            .validators(validator1)
            .dtoMapper(dtoMapper1)
            .jpaSpecificationExecutor(specificationExecutor1)
            .queryDslSpecificationExecutor(queryDslSpecificationExecutor1)
            .entityJoinHandlers(entityJoinHandler1)
            .build()

        assertThat(searchDescriptor1.id).isEqualTo("person1")
        assertThat(searchDescriptor1.entityClass).isEqualTo(entityClass)
        assertThat(searchDescriptor1.validators).containsExactly(validator1)
        assertThat(searchDescriptor1.dtoMapper).isEqualTo(dtoMapper1)
        assertThat(searchDescriptor1.jpaSpecificationExecutor).isEqualTo(specificationExecutor1)
        assertThat(searchDescriptor1.queryDslSpecificationExecutor).isEqualTo(queryDslSpecificationExecutor1)
        assertThat(searchDescriptor1.entityJoinHandlers).containsExactly(entityJoinHandler1)

        val validator2 = mock<DataSearchValidator>()
        val dtoMapper2 = PersonDtoMapper()
        val specificationExecutor2 = PersonRepositoryImpl(entityClass, mockEntityManager(entityClass, false))
        val queryDslSpecificationExecutor2 = mock<QueryDslSpecificationExecutor<Person>>()
        val entityJoinHandler2 = DefaultEntityJoinHandler()
        val searchDescriptor2 = SearchDescriptorBuilder(entityClass)
            .id("person2")
            .validators(validator2)
            .dtoMapper(dtoMapper2)
            .jpaSpecificationExecutor(specificationExecutor2)
            .queryDslSpecificationExecutor(queryDslSpecificationExecutor2)
            .entityJoinHandlers(entityJoinHandler2)
            .build()

        assertThat(searchDescriptor2.id).isEqualTo("person2")
        assertThat(searchDescriptor2.entityClass).isEqualTo(entityClass)
        assertThat(searchDescriptor2.validators).containsExactly(validator2)
        assertThat(searchDescriptor2.dtoMapper).isEqualTo(dtoMapper2)
        assertThat(searchDescriptor2.jpaSpecificationExecutor).isEqualTo(specificationExecutor2)
        assertThat(searchDescriptor2.queryDslSpecificationExecutor).isEqualTo(queryDslSpecificationExecutor2)
        assertThat(searchDescriptor2.entityJoinHandlers).containsExactly(entityJoinHandler2)
    }

    @Test
    fun throw_exception_when_JpaSpecificationExecutor_is_not_defined() {
        assertThatThrownBy { SearchDescriptorBuilder.builder<Person>().build() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("JPA SpecificationExecutor is required. JpaSpecificationExecutorFactory is not initialized with an EntityManager. Use 'jpaSpecificationExecutor' method.")

        assertThatThrownBy { SearchDescriptorBuilder(Person::class.java).build() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("JPA SpecificationExecutor is required. JpaSpecificationExecutorFactory is not initialized with an EntityManager. Use 'jpaSpecificationExecutor' method.")
    }

    private fun mockEntityManager(entityClass: Class<Person>, open: Boolean): EntityManager {
        val entityManager = mock<EntityManager>(lenient = true)
        val metamodel = mock<Metamodel>(lenient = true)
        val type = mock<EntityType<Person>>(lenient = true)
        whenever(entityManager.metamodel).thenReturn(metamodel)
        whenever(metamodel.managedType(entityClass)).thenReturn(type)
        whenever(type.name).thenReturn(Person::class.java.simpleName)
        whenever(entityManager.delegate).thenReturn(mock())
        if (open) {
            whenever(entityManager.isOpen).thenReturn(open)
        }
        return entityManager
    }

}