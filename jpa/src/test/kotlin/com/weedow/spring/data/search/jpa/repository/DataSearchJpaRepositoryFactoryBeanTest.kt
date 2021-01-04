package com.weedow.spring.data.search.jpa.repository

import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.repository.DataSearchBaseRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.persistence.EntityManager
import javax.persistence.metamodel.IdentifiableType
import javax.persistence.metamodel.Metamodel

internal class DataSearchJpaRepositoryFactoryBeanTest {

    @Test
    fun createRepositoryFactory() {
        val repositoryInterface = PersonRepository::class.java
        val dataSearchContext = mock<DataSearchContext>()
        val entityManager = mock<EntityManager> {
            val delegate = mock<EntityManager>()
            on { this.delegate }.thenReturn(delegate)

            val identifiableType = mock<IdentifiableType<Person>>()
            val metamodel = mock<Metamodel> {
                on { this.managedType(Person::class.java) }.thenReturn(identifiableType)
            }
            on { this.metamodel }.thenReturn(metamodel)
        }

        val factoryBean = DataSearchJpaRepositoryFactoryBean(repositoryInterface, dataSearchContext)
        factoryBean.setEntityManager(entityManager)
        factoryBean.setBeanClassLoader(javaClass.classLoader)
        factoryBean.afterPropertiesSet()

        assertThat(factoryBean.objectType).isEqualTo(PersonRepository::class.java)
        assertThat(factoryBean.`object`.toString()).startsWith(DataSearchJpaRepositoryImpl::class.qualifiedName) // 'object' is a Proxy
        assertThat(factoryBean.entityInformation.javaType).isEqualTo(Person::class.java)
        assertThat(factoryBean.repositoryInformation.repositoryBaseClass).isEqualTo(DataSearchJpaRepositoryImpl::class.java)
        assertThat(factoryBean.repositoryInformation.repositoryInterface).isEqualTo(PersonRepository::class.java)
        assertThat(factoryBean.repositoryInformation.domainType).isEqualTo(Person::class.java)
        assertThat(factoryBean.repositoryInformation.idType).isEqualTo(Long::class.javaObjectType)
    }

    interface PersonRepository : DataSearchBaseRepository<Person, Long>

}