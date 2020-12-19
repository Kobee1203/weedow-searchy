/*
package com.weedow.spring.data.search.querydsl.mongodb

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory
import org.springframework.data.mongodb.repository.support.QuerydslMongoPredicateExecutor
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport

class MongoQueryDslSpecificationExecutorFactory(
        private val mongoOperations: MongoOperations,
        private val dataSearchContext: DataSearchContext
) : AbstractQueryDslSpecificationExecutorFactory(dataSearchContext) {

    override fun <T, ID> newQueryDslSpecificationExecutor(dataSearchContext: DataSearchContext, entityInformation: EntityInformation<T, ID>, entityPathResolver: EntityPathResolver): QueryDslSpecificationExecutor<T> {
        return MongoQueryDslPredicateExecutor(dataSearchContext, entityInformation as MongoEntityInformation<T, *>, mongoOperations, entityPathResolver)
    }

    override fun getRepositoryFactory(): RepositoryFactorySupport {
        return MongoRepositoryFactory(mongoOperations)
    }

}
*/