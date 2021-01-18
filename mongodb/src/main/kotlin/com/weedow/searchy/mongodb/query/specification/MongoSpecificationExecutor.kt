package com.weedow.searchy.mongodb.query.specification

import com.querydsl.core.types.EntityPath
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.mongodb.query.MongoQueryBuilder
import com.weedow.searchy.query.querytype.QEntityRootImpl
import com.weedow.searchy.query.specification.Specification
import com.weedow.searchy.query.specification.SpecificationExecutor
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.repository.core.EntityInformation

/**
 * MongoDB [SpecificationExecutor] implementation.
 *
 * _Inspired by the class [org.springframework.data.mongodb.repository.support.QuerydslMongoPredicateExecutor]._
 *
 * @param searchyContext [SearchyContext]
 * @param entityInformation [MongoEntityInformation]
 * @param mongoOperations [MongoOperations]
 * @param resolver [EntityPathResolver]
 */
open class MongoSpecificationExecutor<T>(
    private val searchyContext: SearchyContext,
    private val entityInformation: MongoEntityInformation<T, *>,
    private val mongoOperations: MongoOperations,
    resolver: EntityPathResolver
) : SpecificationExecutor<T> {

    private val path: EntityPath<T> = resolver.createPath(entityInformation.javaType)

    override fun findAll(specification: Specification<T>?): List<T> {
        return createQuery(specification).fetch()
    }

    /**
     * Creates a new [SpringDataMongodbQuery] for the given [Specification].
     *
     * @param specification
     * @return the Querydsl [SpringDataMongodbQuery].
     */
    protected fun createQuery(specification: Specification<T>?): SpringDataMongodbQuery<T> {
        var query = doCreateQuery()

        if (specification != null) {
            val predicate = specification.toPredicate(createQueryBuilder(query))
            if (predicate != Specification.NO_PREDICATE) {
                query = query.where(predicate)
            }
        }

        return query
    }

    private fun doCreateQuery(): SpringDataMongodbQuery<T> {
        return SpringDataMongodbQuery(mongoOperations, typeInformation().javaType)
    }

    private fun createQueryBuilder(query: SpringDataMongodbQuery<T>) =
        MongoQueryBuilder(searchyContext, query, QEntityRootImpl(searchyContext.get(path.type)))

    private fun typeInformation(): EntityInformation<T, *> {
        return entityInformation
    }

}