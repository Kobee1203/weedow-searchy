package com.weedow.searchy.mongodb.repository

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.mongodb.query.specification.MongoSpecificationExecutorFactory
import com.weedow.searchy.query.specification.Specification
import com.weedow.searchy.query.specification.SpecificationExecutor
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.repository.SearchyBaseRepository
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

/**
 * Default MongoDB Searchy Repository implementation.
 *
 * @param entityInformation must not be null
 * @param mongoOperations must not be null
 * @param searchyContext must not be null
 * @param T The type of the entity to handle
 * @param ID The type of the entity's identifier
 */
@Transactional(readOnly = true)
class MongoSearchyRepositoryImpl<T, ID : Serializable>(
    entityInformation: MongoEntityInformation<T, ID>,
    mongoOperations: MongoOperations,
    searchyContext: SearchyContext
) : SimpleMongoRepository<T, ID>(entityInformation, mongoOperations),
    SearchyBaseRepository<T, ID> {

    private val factory: SpecificationExecutorFactory = MongoSpecificationExecutorFactory(mongoOperations, searchyContext)
    private val specificationExecutor: SpecificationExecutor<T> = factory.getSpecificationExecutor(entityInformation.javaType)

    override fun findAll(specification: Specification<T>?): List<T> {
        return specificationExecutor.findAll(specification)
    }

}