package com.weedow.searchy.service

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.expression.RootExpression
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationService
import com.weedow.searchy.utils.klogger

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

    override fun <T> findAll(rootExpression: RootExpression<T>, searchyDescriptor: SearchyDescriptor<T>): List<T> {
        val specification = specificationService.createSpecification(rootExpression, searchyDescriptor)

        val specificationExecutor = searchyDescriptor.specificationExecutor
            ?: specificationExecutorFactory.getSpecificationExecutor(searchyDescriptor.entityClass)

        return specificationExecutor.findAll(specification)
    }

}