package com.weedow.searchy.jpa.repository

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.querytype.QEntity
import com.weedow.searchy.query.specification.Specification
import com.weedow.searchy.repository.SearchyBaseRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Query
import javax.persistence.metamodel.IdentifiableType
import javax.persistence.metamodel.Metamodel

@ExtendWith(MockitoExtension::class)
internal class JpaSearchyRepositoryImplTest {

    @Test
    fun findAll() {
        val domainClass = Person::class.java

        val resultList = listOf(Person("John", "Doe"))

        val em = mockEntityManager(domainClass, resultList)

        val searchyContext = mock<SearchyContext> {
            val qEntity = mock<QEntity<Person>>()
            on { this.get(Person::class.java) }.thenReturn(qEntity)
        }

        val repository: SearchyBaseRepository<Person, Long> = JpaSearchyRepositoryImpl(domainClass, em, searchyContext)

        val specification: Specification<Person> = mock()
        val result = repository.findAll(specification)

        assertThat(result).isSameAs(resultList)
    }

    private fun mockEntityManager(domainClass: Class<Person>, resultList: List<Person>): EntityManager {
        return mock {
            val delegate = mock<EntityManager>()
            on { this.delegate }.thenReturn(delegate)

            val identifiableType = mock<IdentifiableType<Person>>()
            val metamodel = mock<Metamodel> {
                on { managedType(domainClass) }.thenReturn(identifiableType)
            }
            on { this.metamodel }.thenReturn(metamodel)

            val entityManagerFactory = mock<EntityManagerFactory> {
                on { properties }.thenReturn(emptyMap())
            }
            on { this.entityManagerFactory }.thenReturn(entityManagerFactory)

            val query = mock<Query> {
                on { this.resultList }.thenReturn(resultList)
            }
            on { createQuery("select person\nfrom Person person") }.thenReturn(query)
        }
    }
}