package com.weedow.spring.data.search

import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.query.specification.AbstractSpecificationExecutorFactory
import com.weedow.spring.data.search.query.specification.SpecificationExecutor
import org.apache.commons.lang3.reflect.FieldUtils
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import javax.persistence.EmbeddedId
import javax.persistence.Id
import javax.persistence.IdClass

class TestSpecificationExecutorFactory(
    dataSearchContext: DataSearchContext
) : AbstractSpecificationExecutorFactory(dataSearchContext) {

    override fun <T, ID> newSpecificationExecutor(
        dataSearchContext: DataSearchContext,
        entityInformation: EntityInformation<T, ID>,
        entityPathResolver: EntityPathResolver
    ): SpecificationExecutor<T> {
        return mock()
    }

    override fun getRepositoryFactory(): RepositoryFactorySupport {
        return mock()
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