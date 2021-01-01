package com.weedow.spring.data.search.jpa.repository

import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.jpa.query.specification.JpaSpecificationExecutorFactory
import com.weedow.spring.data.search.query.specification.Specification
import com.weedow.spring.data.search.query.specification.SpecificationExecutor
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
import com.weedow.spring.data.search.repository.DataSearchBaseRepository
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable
import javax.persistence.EntityManager

/**
 * Default Data Search JPA Repository implementation.
 *
 * @param entityInformation must not be null
 * @param entityManager must not be null
 * @param dataSearchContext must not be null
 * @param T The type of the entity to handle
 * @param ID The type of the entity's identifier
 */
@Transactional(readOnly = true)
class DataSearchJpaRepositoryImpl<T, ID : Serializable>(
    entityInformation: JpaEntityInformation<T, *>,
    entityManager: EntityManager,
    dataSearchContext: DataSearchContext
) : SimpleJpaRepository<T, ID>(entityInformation, entityManager),
    DataSearchBaseRepository<T, ID> {

    private val factory: SpecificationExecutorFactory = JpaSpecificationExecutorFactory(entityManager, dataSearchContext)
    private val specificationExecutor: SpecificationExecutor<T> = factory.getSpecificationExecutor(domainClass)

    constructor(
        domainClass: Class<T>,
        em: EntityManager,
        dataSearchContext: DataSearchContext
    ) : this(
        JpaEntityInformationSupport.getEntityInformation(domainClass, em),
        em,
        dataSearchContext
    )

    override fun findAll(specification: Specification<T>?): List<T> {
        return specificationExecutor.findAll(specification)
    }

}