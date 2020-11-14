package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.specification.JpaSpecificationService
import com.weedow.spring.data.search.utils.klogger

/**
 * Default [EntitySearchService] implementation.
 */
class EntitySearchServiceImpl(
        private val jpaSpecificationService: JpaSpecificationService,
        private val entityJoinManager: EntityJoinManager
) : EntitySearchService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized EntitySearchService: {}", this)
    }

    override fun <T> findAll(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): List<T> {
        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)
        val specification = jpaSpecificationService.createSpecification(rootExpression, entityJoins)
        return searchDescriptor.jpaSpecificationExecutor.findAll(specification)
    }

}