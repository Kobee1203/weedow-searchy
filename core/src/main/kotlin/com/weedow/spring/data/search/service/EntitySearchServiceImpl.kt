package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
import com.weedow.spring.data.search.query.specification.SpecificationService
import com.weedow.spring.data.search.utils.klogger

/**
 * Default [EntitySearchService] implementation.
 *
 * @param specificationService [SpecificationService]
 * @param specificationExecutorFactory [SpecificationExecutorFactory]
 */
class EntitySearchServiceImpl(
    private val specificationService: SpecificationService,
    private val specificationExecutorFactory: SpecificationExecutorFactory
) : EntitySearchService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized EntitySearchService: {}", this::class.qualifiedName)
    }

    override fun <T> findAll(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): List<T> {
        val specification = specificationService.createSpecification(rootExpression, searchDescriptor)

        val specificationExecutor = searchDescriptor.specificationExecutor
            ?: specificationExecutorFactory.getSpecificationExecutor(searchDescriptor.entityClass)

        return specificationExecutor.findAll(specification)
    }

}