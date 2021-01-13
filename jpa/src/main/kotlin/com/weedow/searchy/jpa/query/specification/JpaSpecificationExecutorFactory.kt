package com.weedow.searchy.jpa.query.specification

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.specification.AbstractSpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutor
import org.apache.commons.lang3.reflect.FieldUtils
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import javax.persistence.EmbeddedId
import javax.persistence.EntityManager
import javax.persistence.Id
import javax.persistence.IdClass

/**
 * JPA [SpecificationExecutorFactory][com.weedow.searchy.query.specification.SpecificationExecutorFactory] implementation.
 *
 * @param entityManager [EntityManager]
 * @param searchyContext [SearchyContext]
 */
class JpaSpecificationExecutorFactory(
    private val entityManager: EntityManager,
    searchyContext: SearchyContext
) : AbstractSpecificationExecutorFactory(searchyContext) {

    override fun <T, ID> newSpecificationExecutor(
        searchyContext: SearchyContext,
        entityInformation: EntityInformation<T, ID>,
        entityPathResolver: EntityPathResolver
    ): SpecificationExecutor<T> {
        return JpaSpecificationExecutor(
            searchyContext,
            entityInformation as JpaEntityInformation<T, *>,
            entityManager,
            entityPathResolver,
            null
        )
    }

    override fun getRepositoryFactory(): RepositoryFactorySupport {
        return JpaRepositoryFactory(entityManager)
    }

    override fun <T> findPrimaryKeyClass(entityClass: Class<T>, default: () -> Class<*>): Class<*> {
        val idClassAnnotation = entityClass.getAnnotation(IdClass::class.java)
        if (idClassAnnotation != null) {
            return idClassAnnotation.value.java
        }

        val embeddedFields = FieldUtils.getFieldsWithAnnotation(entityClass, EmbeddedId::class.java)
        if (embeddedFields.isNotEmpty()) {
            return embeddedFields[0].type
        }

        val idFields = FieldUtils.getFieldsWithAnnotation(entityClass, Id::class.java)
        if (idFields.isNotEmpty()) {
            return idFields[0].type
        }

        return super.findPrimaryKeyClass(entityClass, default)
    }

}