package com.weedow.searchy.query.specification

import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.SafeEntityPathResolver
import com.weedow.searchy.utils.EntityUtils
import org.apache.commons.lang3.reflect.FieldUtils
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.repository.core.EntityInformation
import org.springframework.data.repository.core.support.RepositoryFactorySupport

/**
 * Base class for [SpecificationExecutorFactory] implementations.
 *
 * @param searchyContext [SearchyContext]
 */
abstract class AbstractSpecificationExecutorFactory(
    private val searchyContext: SearchyContext
) : SpecificationExecutorFactory {

    override fun <T> getSpecificationExecutor(entityClass: Class<T>): SpecificationExecutor<T> {
        val primaryKeyClass = findPrimaryKeyClass(entityClass)
        val entityInformation = getEntityInformation(entityClass, primaryKeyClass)

        val entityPathResolver = getEntityPathResolver()

        return newSpecificationExecutor(searchyContext, entityInformation, entityPathResolver)
    }

    protected abstract fun <T, ID> newSpecificationExecutor(
        searchyContext: SearchyContext,
        entityInformation: EntityInformation<T, ID>,
        entityPathResolver: EntityPathResolver
    ): SpecificationExecutor<T>

    protected abstract fun getRepositoryFactory(): RepositoryFactorySupport

    /**
     * Gets the primary key class of the given Entity class by searching for the primary key field.
     *
     * If the primary key field is not found the method returns the result of the `default` lambda.
     *
     * @param entityClass Entity class for which the primary key class is retrieved
     * @param default lambda used if the primary key field is not found
     */
    protected open fun <T> findPrimaryKeyClass(entityClass: Class<T>, default: () -> Class<*> = { Long::class.java }): Class<*> {
        val idField = EntityUtils.getFieldWithAnnotation(entityClass, org.springframework.data.annotation.Id::class.java)
        if (idField != null) {
            return idField.type
        }

        val field = FieldUtils.getField(entityClass, "id", true)
        if (field != null) {
            return field.type
        }

        return default()
    }

    protected open fun <T, ID> getEntityInformation(
        entityClass: Class<T>,
        @Suppress("UNUSED_PARAMETER") primaryKeyClass: Class<ID>
    ): EntityInformation<T, ID> {
        val repositoryFactory = getRepositoryFactory()
        return repositoryFactory.getEntityInformation(entityClass)
    }

    protected open fun getEntityPathResolver(): EntityPathResolver {
        return SafeEntityPathResolver.INSTANCE
    }

}