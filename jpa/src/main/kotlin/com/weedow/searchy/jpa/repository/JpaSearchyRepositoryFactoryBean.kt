package com.weedow.searchy.jpa.repository

import com.weedow.searchy.context.SearchyContext
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
 * Special adapter for Springs `org.springframework.beans.factory.FactoryBean` interface to allow easy setup of weedow-searchy repository
 * factories via Spring configuration.
 *
 * **Example:**
 *
 * ```
 * @SpringBootApplication
 * @EnableJpaRepositories(value = ["com.sample.repository"], repositoryFactoryBeanClass = JpaSearchyRepositoryFactoryBean::class)
 * class SampleApplication
 *
 * fun main(args: Array<String>) {
 *     runApplication<SampleApplication>(*args)
 * }
 * ```
 *
 * @param repositoryInterface must not be null
 * @param searchyContext must not be null
 * @param R The type of the repository
 * @param T The domain type the repository manages
 * @param ID The type of the id of the entity the repository manages
 */
class JpaSearchyRepositoryFactoryBean<R : Repository<T, ID>, T, ID : Serializable>(
    repositoryInterface: Class<R>,
    private val searchyContext: SearchyContext
) : JpaRepositoryFactoryBean<R, T, ID>(repositoryInterface) {

    override fun createRepositoryFactory(em: EntityManager): RepositoryFactorySupport {
        return JpaSearchyRepositoryFactory(em, searchyContext)
    }

    private class JpaSearchyRepositoryFactory(
        em: EntityManager,
        private val searchyContext: SearchyContext
    ) : JpaRepositoryFactory(em) {
        override fun getTargetRepository(information: RepositoryInformation, entityManager: EntityManager): JpaRepositoryImplementation<*, *> {
            val entityInformation: JpaEntityInformation<*, Serializable> = getEntityInformation(information.domainType)
            val repository = getTargetRepositoryViaReflection<Any>(information, entityInformation, entityManager, searchyContext)

            Assert.isInstanceOf(JpaRepositoryImplementation::class.java, repository)

            return repository as JpaRepositoryImplementation<*, *>
        }

        override fun getRepositoryBaseClass(metadata: RepositoryMetadata): Class<*> {
            return JpaSearchyRepositoryImpl::class.java
        }
    }
}