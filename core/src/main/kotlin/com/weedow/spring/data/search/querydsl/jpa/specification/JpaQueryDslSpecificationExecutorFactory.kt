package com.weedow.spring.data.search.querydsl.jpa.specification

import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.specification.AbstractQueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutor
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

class JpaQueryDslSpecificationExecutorFactory(
        private val entityManager: EntityManager,
        private val dataSearchContext: DataSearchContext,
) : AbstractQueryDslSpecificationExecutorFactory(dataSearchContext) {

    override fun <T, ID> newQueryDslSpecificationExecutor(dataSearchContext: DataSearchContext, entityInformation: EntityInformation<T, ID>, entityPathResolver: EntityPathResolver): QueryDslSpecificationExecutor<T> {
        return JpaQueryDslSpecificationExecutor(dataSearchContext, entityInformation as JpaEntityInformation<T, *>, entityManager, entityPathResolver, null)
    }

    override fun getRepositoryFactory(): RepositoryFactorySupport {
        return JpaRepositoryFactory(entityManager)
    }

    override fun <T> findPrimaryKeyClass(domainClass: Class<T>, default: () -> Class<*>): Class<*> {
        val idClassAnnotation = domainClass.getAnnotation(IdClass::class.java)
        if (idClassAnnotation != null) {
            return idClassAnnotation.value.java
        }

        val embeddedFields = FieldUtils.getFieldsWithAnnotation(domainClass, EmbeddedId::class.java)
        if (embeddedFields.isNotEmpty()) {
            return embeddedFields[0].type
        }

        val idFields = FieldUtils.getFieldsWithAnnotation(domainClass, Id::class.java)
        if (idFields.isNotEmpty()) {
            return idFields[0].type
        }

        return super.findPrimaryKeyClass(domainClass, default)
    }

}