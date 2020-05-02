package com.weedow.spring.data.search.descriptor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.config.JpaSpecificationExecutorFactory
import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.example.dto.PersonDtoMapper
import com.weedow.spring.data.search.example.model.Person
import com.weedow.spring.data.search.example.repository.PersonRepositoryImpl
import com.weedow.spring.data.search.join.DefaultEntityJoinHandler
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager
import javax.persistence.metamodel.EntityType
import javax.persistence.metamodel.Metamodel

internal class SearchDescriptorBuilderTest {

    @Test
    fun build_SearchDescriptor_with_default_values_and_JpaSpecificationExecutorFactory_initialized() {
        val entityClass = Person::class.java

        val entityManager = mockEntityManager(entityClass)

        JpaSpecificationExecutorFactory.init(entityManager)
        try {
            val searchDescriptor1 = SearchDescriptorBuilder.builder<Person>()
                    .build()

            assertThat(searchDescriptor1.id).isEqualTo("person")
            assertThat(searchDescriptor1.entityClass).isEqualTo(entityClass)
            assertThat(searchDescriptor1.dtoMapper).isEqualTo(DefaultDtoMapper<Person>())
            assertThat(searchDescriptor1.jpaSpecificationExecutor).isNotNull
            assertThat(searchDescriptor1.jpaSpecificationExecutor).isInstanceOf(SimpleJpaRepository::class.java)
            assertThat(searchDescriptor1.entityJoinHandlers).isEmpty()

            val searchDescriptor2 = SearchDescriptorBuilder(entityClass)
                    .build()

            assertThat(searchDescriptor2.id).isEqualTo("person")
            assertThat(searchDescriptor2.entityClass).isEqualTo(entityClass)
            assertThat(searchDescriptor2.dtoMapper).isEqualTo(DefaultDtoMapper<Person>())
            assertThat(searchDescriptor2.jpaSpecificationExecutor).isNotNull
            assertThat(searchDescriptor2.jpaSpecificationExecutor).isInstanceOf(SimpleJpaRepository::class.java)
            assertThat(searchDescriptor2.entityJoinHandlers).isEmpty()

        } finally {
            JpaSpecificationExecutorFactory.reset()
        }
    }

    @Test
    fun build_SearchDescriptor_with_custom_values() {
        val entityClass = Person::class.java

        val dtoMapper1 = PersonDtoMapper()
        val specificationExecutor1 = PersonRepositoryImpl(entityClass, mockEntityManager(entityClass))
        val entityJoinHandlers1 = DefaultEntityJoinHandler<Person>()
        val searchDescriptor1 = SearchDescriptorBuilder.builder<Person>()
                .id("person1")
                .dtoMapper(dtoMapper1)
                .jpaSpecificationExecutor(specificationExecutor1)
                .entityJoinHandlers(entityJoinHandlers1)
                .build()

        assertThat(searchDescriptor1.id).isEqualTo("person1")
        assertThat(searchDescriptor1.entityClass).isEqualTo(entityClass)
        assertThat(searchDescriptor1.dtoMapper).isEqualTo(dtoMapper1)
        assertThat(searchDescriptor1.jpaSpecificationExecutor).isEqualTo(specificationExecutor1)
        assertThat(searchDescriptor1.entityJoinHandlers).containsExactly(entityJoinHandlers1)

        val dtoMapper2 = PersonDtoMapper()
        val specificationExecutor2 = PersonRepositoryImpl(entityClass, mockEntityManager(entityClass))
        val entityJoinHandlers2 = DefaultEntityJoinHandler<Person>()
        val searchDescriptor2 = SearchDescriptorBuilder(entityClass)
                .id("person2")
                .dtoMapper(dtoMapper2)
                .jpaSpecificationExecutor(specificationExecutor2)
                .entityJoinHandlers(entityJoinHandlers2)
                .build()

        assertThat(searchDescriptor2.id).isEqualTo("person2")
        assertThat(searchDescriptor2.entityClass).isEqualTo(entityClass)
        assertThat(searchDescriptor2.dtoMapper).isEqualTo(dtoMapper2)
        assertThat(searchDescriptor2.jpaSpecificationExecutor).isEqualTo(specificationExecutor2)
        assertThat(searchDescriptor2.entityJoinHandlers).containsExactly(entityJoinHandlers2)
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

    private fun mockEntityManager(entityClass: Class<Person>): EntityManager {
        val entityManager = mock<EntityManager>()
        val metamodel = mock<Metamodel>()
        val type = mock<EntityType<Person>>()
        whenever(entityManager.metamodel).thenReturn(metamodel)
        whenever(metamodel.managedType(entityClass)).thenReturn(type)
        whenever(type.name).thenReturn(Person::class.java.simpleName)
        whenever(entityManager.delegate).thenReturn(mock())
        whenever(entityManager.isOpen).thenReturn(true)
        return entityManager
    }

}