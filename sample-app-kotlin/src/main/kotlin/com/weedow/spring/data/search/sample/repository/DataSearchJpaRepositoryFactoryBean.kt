package com.weedow.spring.data.search.sample.repository

import com.weedow.spring.data.search.context.DataSearchContext
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import org.springframework.util.Assert
import java.io.Serializable
import javax.persistence.EntityManager

/**
 * Special adapter for Springs `org.springframework.beans.factory.FactoryBean` interface to allow easy setup of spring-data-search repository
 * factories via Spring configuration.
 *
 * @param repositoryInterface must not be null
 * @param dataSearchContext must not be null
 * @param R The type of the repository
 * @param T The domain type the repository manages
 * @param ID The type of the id of the entity the repository manages
 */
class DataSearchJpaRepositoryFactoryBean<R : Repository<T, ID>, T, ID : Serializable>(
    repositoryInterface: Class<R>,
    private val dataSearchContext: DataSearchContext
) : JpaRepositoryFactoryBean<R, T, ID>(repositoryInterface) {

    override fun createRepositoryFactory(em: EntityManager): RepositoryFactorySupport {
        return DataSearchJpaRepositoryFactory(em, dataSearchContext)
    }

    private class DataSearchJpaRepositoryFactory(
        em: EntityManager,
        private val dataSearchContext: DataSearchContext
    ) : JpaRepositoryFactory(em) {
        override fun getTargetRepository(information: RepositoryInformation, entityManager: EntityManager): JpaRepositoryImplementation<*, *> {
            val entityInformation: JpaEntityInformation<*, Serializable> = getEntityInformation(information.domainType)
            val repository = getTargetRepositoryViaReflection<Any>(information, entityInformation, entityManager, dataSearchContext)

            Assert.isInstanceOf(JpaRepositoryImplementation::class.java, repository)

            return repository as JpaRepositoryImplementation<*, *>
        }

        override fun getRepositoryBaseClass(metadata: RepositoryMetadata): Class<*> {
            return DataSearchJpaRepositoryImpl::class.java
        }
    }
}