package com.weedow.searchy.mongodb.repository

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.repository.SearchyBaseRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty

internal class MongoSearchyRepositoryFactoryBeanTest {

    @Test
    fun createRepositoryFactory() {
        val repositoryInterface = PersonRepository::class.java
        val searchyContext = mock<SearchyContext>()
        val mongoOperations = mock<MongoOperations> {
            val converter = mock<MongoConverter> {
                val mappingContext = mock<MappingContext<out MongoPersistentEntity<*>, MongoPersistentProperty>> {
                    val persistentEntity = mock<MongoPersistentEntity<*>> {
                        on { this.type }.thenReturn(Person::class.java)
                    }
                    on { this.getRequiredPersistentEntity(Person::class.java) }.thenReturn(persistentEntity)
                }
                on { this.mappingContext }.thenReturn(mappingContext)
            }
            on { this.converter }.thenReturn(converter)
        }

        val factoryBean = MongoSearchyRepositoryFactoryBean(repositoryInterface, searchyContext)
        factoryBean.setMongoOperations(mongoOperations)
        factoryBean.setBeanClassLoader(javaClass.classLoader)
        factoryBean.afterPropertiesSet()

        Assertions.assertThat(factoryBean.objectType).isEqualTo(PersonRepository::class.java)
        Assertions.assertThat(factoryBean.`object`.toString()).startsWith(MongoSearchyRepositoryImpl::class.qualifiedName) // 'object' is a Proxy
        Assertions.assertThat(factoryBean.entityInformation.javaType).isEqualTo(Person::class.java)
        Assertions.assertThat(factoryBean.repositoryInformation.repositoryBaseClass).isEqualTo(MongoSearchyRepositoryImpl::class.java)
        Assertions.assertThat(factoryBean.repositoryInformation.repositoryInterface).isEqualTo(PersonRepository::class.java)
        Assertions.assertThat(factoryBean.repositoryInformation.domainType).isEqualTo(Person::class.java)
        Assertions.assertThat(factoryBean.repositoryInformation.idType).isEqualTo(String::class.javaObjectType)
    }

    interface PersonRepository : SearchyBaseRepository<Person, String>

    @Document
    internal class Person {
        @Id
        var id: String? = null
    }
}