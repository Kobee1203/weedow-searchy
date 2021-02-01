package com.weedow.searchy.mongodb.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.querytype.QEntity
import com.weedow.searchy.query.specification.Specification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.mongodb.core.ExecutableFindOperation
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty
import org.springframework.data.mongodb.core.query.BasicQuery
import org.springframework.data.mongodb.repository.query.MongoEntityInformation

@ExtendWith(MockitoExtension::class)
internal class MongoSearchyRepositoryImplTest {

    @Test
    fun findAll() {
        val domainClass = Person::class.java

        val resultList = listOf(Person())

        val entityInformation = mock<MongoEntityInformation<Person, String>> {
            on { this.javaType }.thenReturn(domainClass)
        }

        val mongoOperations = mockMongoOperations(domainClass, resultList)

        val searchyContext = mock<SearchyContext> {
            val qEntity = mock<QEntity<Person>>()
            on { this.get(Person::class.java) }.thenReturn(qEntity)
        }

        val repository = MongoSearchyRepositoryImpl(entityInformation, mongoOperations, searchyContext)

        val specification: Specification<Person> = mock()
        val result = repository.findAll(specification)

        assertThat(result).isSameAs(resultList)
    }

    private fun mockMongoOperations(domainClass: Class<Person>, resultList: List<Person>): MongoOperations {
        return mock {
            val converter = mock<MongoConverter> {
                val mappingContext = mock<MappingContext<out MongoPersistentEntity<*>, MongoPersistentProperty>> {
                    val persistentEntity = mock<MongoPersistentEntity<*>> {
                        on { this.type }.thenReturn(domainClass)
                    }
                    on { this.getRequiredPersistentEntity(domainClass) }.thenReturn(persistentEntity)
                }
                on { this.mappingContext }.thenReturn(mappingContext)
            }
            on { this.converter }.thenReturn(converter)

            on { this.getCollectionName(domainClass) }.thenReturn("collection_name")

            val query = mock<ExecutableFindOperation.ExecutableFind<Person>> {
                val terminatingFind = mock<ExecutableFindOperation.TerminatingFind<Person>> {
                    on { this.all() }.thenReturn(resultList)
                }
                val findWithProjection = mock<ExecutableFindOperation.FindWithProjection<Person>> {
                    on { this.matching(any<BasicQuery>()) }.thenReturn(terminatingFind)
                }
                on { this.inCollection("collection_name") }.thenReturn(findWithProjection)
            }
            on { this.query(domainClass) }.thenReturn(query)
        }
    }

    internal class Person
}