package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.expression.RootExpression
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.specification.JpaSpecificationService
import com.weedow.spring.data.search.utils.klogger
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

/**
 * Default [DataSearchService] implementation.
 *
 * This implementation uses transactions for any calls to methods of this class.
 * The transactions are read-only by default.
 */
@Transactional(readOnly = true)
class DataSearchServiceImpl(
        private val jpaSpecificationService: JpaSpecificationService,
        private val entityJoinManager: EntityJoinManager
) : DataSearchService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized DataSearchService: {}", this)
    }

    override fun <T> findAll(rootExpression: RootExpression<T>, searchDescriptor: SearchDescriptor<T>): List<*> {
        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)
        val specification = jpaSpecificationService.createSpecification(rootExpression, entityJoins)
        val entities = searchDescriptor.jpaSpecificationExecutor.findAll(specification)

        return convertToDto(entities, searchDescriptor)
    }

    private fun <T> convertToDto(result: List<T>, searchDescriptor: SearchDescriptor<T>): List<*> {
        @Suppress("UNCHECKED_CAST")
        val dtoMapper = searchDescriptor.dtoMapper as DtoMapper<Any?, Any?>

        return result.stream()
                .map { entity -> dtoMapper.map(entity) }
                .collect(Collectors.toList())
    }

}