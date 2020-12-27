package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationService
import com.weedow.spring.data.search.utils.klogger

/**
 * Default [EntitySearchService] implementation.
 *
 * @param queryDslSpecificationService [QueryDslSpecificationService]
 * @param queryDslSpecificationExecutorFactory [QueryDslSpecificationExecutorFactory]
 */
class EntitySearchServiceImpl(
    private val queryDslSpecificationService: QueryDslSpecificationService,
    private val queryDslSpecificationExecutorFactory: QueryDslSpecificationExecutorFactory
) : EntitySearchService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized EntitySearchService: {}", this::class.qualifiedName)
    }

    override fun <T> findAll(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): List<T> {
        val specification = queryDslSpecificationService.createSpecification(rootExpression, searchDescriptor)

        val queryDslSpecificationExecutor = searchDescriptor.queryDslSpecificationExecutor
            ?: queryDslSpecificationExecutorFactory.getQueryDslSpecificationExecutor(searchDescriptor.entityClass)

        return queryDslSpecificationExecutor.findAll(specification)
    }

}