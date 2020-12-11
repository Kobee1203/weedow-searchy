package com.weedow.spring.data.search.querydsl.specification

import com.weedow.spring.data.search.querydsl.SafeEntityPathResolver
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.utils.EntityUtils
import org.apache.commons.lang3.reflect.FieldUtils
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport

abstract class AbstractQueryDslSpecificationExecutorFactory(
        private val dataSearchContext: DataSearchContext,
) : QueryDslSpecificationExecutorFactory {

    override fun <T> getQueryDslSpecificationExecutor(domainClass: Class<T>): QueryDslSpecificationExecutor<T> {
        val primaryKeyClass = findPrimaryKeyClass(domainClass)
        val entityInformation = getEntityInformation(domainClass, primaryKeyClass)

        val entityPathResolver = getEntityPathResolver()

        return newQueryDslSpecificationExecutor(dataSearchContext, entityInformation, entityPathResolver)
    }

    protected abstract fun <T, ID> newQueryDslSpecificationExecutor(dataSearchContext: DataSearchContext, entityInformation: EntityInformation<T, ID>, entityPathResolver: EntityPathResolver): QueryDslSpecificationExecutor<T>

    protected abstract fun getRepositoryFactory(): RepositoryFactorySupport

    /**
     * Gets the primary key class of the given Entity class by searching for the primary key field.
     *
     * If the primary key field is not found the method returns the result of the `default` lambda.
     *
     * @param domainClass Entity class for which the primary key class is retrieved
     * @param default lambda used if the primary key field is not found
     */
    protected open fun <T> findPrimaryKeyClass(domainClass: Class<T>, default: () -> Class<*> = { Long::class.java }): Class<*> {
        val idField = EntityUtils.getFieldWithAnnotation(domainClass, org.springframework.data.annotation.Id::class.java)
        if (idField != null) {
            return idField.type
        }

        val field = FieldUtils.getField(domainClass, "id")
        if (field != null) {
            return field.type
        }

        return default()
    }

    protected open fun <T, ID> getEntityInformation(domainClass: Class<T>, @Suppress("UNUSED_PARAMETER") primaryKeyClass: Class<ID>): EntityInformation<T, ID> {
        val repositoryFactory = getRepositoryFactory()
        return repositoryFactory.getEntityInformation(domainClass)
    }

    protected open fun getEntityPathResolver(): EntityPathResolver {
        return SafeEntityPathResolver.INSTANCE
    }

}