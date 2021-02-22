package com.weedow.searchy.mongodb.query.specification

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.specification.AbstractSpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutor
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport

/**
 * MongoDB [SpecificationExecutorFactory][com.weedow.searchy.query.specification.SpecificationExecutorFactory] implementation.
 *
 * @param mongoOperations [MongoOperations]
 * @param searchyContext [SearchyContext]
 */
class MongoSpecificationExecutorFactory(
    private val mongoOperations: MongoOperations,
    searchyContext: SearchyContext
) : AbstractSpecificationExecutorFactory(searchyContext) {

    override fun <T, ID> newSpecificationExecutor(
        searchyContext: SearchyContext,
        entityInformation: EntityInformation<T, ID>,
        entityPathResolver: EntityPathResolver
    ): SpecificationExecutor<T> {
        return MongoSpecificationExecutor(
            searchyContext,
            entityInformation as MongoEntityInformation<T, *>,
            mongoOperations,
            entityPathResolver
        )
    }

    override fun getRepositoryFactory(): RepositoryFactorySupport {
        return MongoRepositoryFactory(mongoOperations)
    }

    override fun <T> findPrimaryKeyClass(entityClass: Class<T>, default: () -> Class<*>): Class<*> {
        return super.findPrimaryKeyClass(entityClass, default)
    }

}