package com.weedow.searchy.mongodb.repository

import com.weedow.searchy.context.SearchyContext
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import java.io.Serializable

/**
 * Special adapter for Springs `org.springframework.beans.factory.FactoryBean` interface to allow easy setup of weedow-searchy repository
 * factories via Spring configuration.
 *
 * **Example:**
 *
 * ```
 * @SpringBootApplication
 * @EnableMongoRepositories(value = ["com.sample.repository"], repositoryFactoryBeanClass = MongoSearchyRepositoryFactoryBean::class)
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
class MongoSearchyRepositoryFactoryBean<R : Repository<T, ID>, T, ID : Serializable>(
    repositoryInterface: Class<R>,
    private val searchyContext: SearchyContext
) : MongoRepositoryFactoryBean<R, T, ID>(repositoryInterface) {

    override fun getFactoryInstance(mongoOperations: MongoOperations): RepositoryFactorySupport {
        return MongoSearchyRepositoryFactory(mongoOperations, searchyContext)
    }

    private class MongoSearchyRepositoryFactory(
        private val mongoOperations: MongoOperations,
        private val searchyContext: SearchyContext
    ) : MongoRepositoryFactory(mongoOperations) {
        override fun getTargetRepository(information: RepositoryInformation): Any {
            val entityInformation: MongoEntityInformation<*, Serializable> = getEntityInformation(information.domainType)
            return getTargetRepositoryViaReflection(information, entityInformation, mongoOperations, searchyContext)
        }

        override fun getRepositoryBaseClass(metadata: RepositoryMetadata): Class<*> {
            return MongoSearchyRepositoryImpl::class.java
        }
    }
}