package com.weedow.searchy.jpa.repository

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.jpa.query.specification.JpaSpecificationExecutorFactory
import com.weedow.searchy.query.specification.Specification
import com.weedow.searchy.query.specification.SpecificationExecutor
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.repository.SearchyBaseRepository
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable
import javax.persistence.EntityManager

/**
 * Default JPA Searchy Repository implementation.
 *
 * @param entityInformation must not be null
 * @param entityManager must not be null
 * @param searchyContext must not be null
 * @param T The type of the entity to handle
 * @param ID The type of the entity's identifier
 */
@Transactional(readOnly = true)
class JpaSearchyRepositoryImpl<T, ID : Serializable>(
    entityInformation: JpaEntityInformation<T, *>,
    entityManager: EntityManager,
    searchyContext: SearchyContext
) : SimpleJpaRepository<T, ID>(entityInformation, entityManager),
    SearchyBaseRepository<T, ID> {

    private val factory: SpecificationExecutorFactory = JpaSpecificationExecutorFactory(entityManager, searchyContext)
    private val specificationExecutor: SpecificationExecutor<T> = factory.getSpecificationExecutor(entityInformation.javaType)

    constructor(
        domainClass: Class<T>,
        em: EntityManager,
        searchyContext: SearchyContext
    ) : this(
        JpaEntityInformationSupport.getEntityInformation(domainClass, em),
        em,
        searchyContext
    )

    override fun findAll(specification: Specification<T>?): List<T> {
        return specificationExecutor.findAll(specification)
    }

}