package com.weedow.searchy.service

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.descriptor.SearchyDescriptorService
import com.weedow.searchy.dto.DtoConverterService
import com.weedow.searchy.exception.SearchyDescriptorNotFound
import com.weedow.searchy.exception.ValidationException
import com.weedow.searchy.expression.ExpressionMapper
import com.weedow.searchy.utils.klogger
import com.weedow.searchy.validation.SearchyValidationService
import org.springframework.transaction.annotation.Transactional

/**
 * Default [SearchyService] implementation.
 *
 * Here are the steps made by this implementation:
 *   - Get the [SearchyDescriptor] from the given SearchyDescriptor Id
 *   - Convert the given map of parameters to [Expression][com.weedow.searchy.expression.Expression]s
 *   - Validate the resulting expressions with the [SearchyDescriptor]
 *   - Find filtered entities from the Expressions
 *   - Convert found entities to DTO
 *
 * This implementation uses transactions for any calls to methods of this class.
 * The transactions are `read-only` by default.
 *
 * @param searchyDescriptorService [SearchyDescriptorService]
 * @param expressionMapper [ExpressionMapper]
 * @param searchyValidationService [SearchyValidationService]
 * @param entitySearchService [EntitySearchService]
 */
@Transactional(readOnly = true)
class SearchyServiceImpl<T, DTO>(
    private val searchyDescriptorService: SearchyDescriptorService,
    private val expressionMapper: ExpressionMapper,
    private val searchyValidationService: SearchyValidationService,
    private val entitySearchService: EntitySearchService,
    private val dtoConverterService: DtoConverterService<T, DTO>
) : SearchyService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized SearchyService: {}", this::class.qualifiedName)
    }

    /**
     * {@inheritDoc}
     * @throws ValidationException when there are any validation errors
     */
    @Throws(SearchyDescriptorNotFound::class, ValidationException::class)
    override fun search(searchyDescriptorId: String, params: Map<String, List<String>>): List<*> {
        // Find Entity Search Descriptor
        val searchyDescriptor = searchyDescriptorService.getSearchyDescriptor(searchyDescriptorId)
            ?: throw SearchyDescriptorNotFound(searchyDescriptorId)

        @Suppress("UNCHECKED_CAST")
        return doSearch(params, searchyDescriptor as SearchyDescriptor<T>)
    }

    private fun doSearch(params: Map<String, List<String>>, searchyDescriptor: SearchyDescriptor<T>): List<*> {
        // Mapping the given parameters to the associated expressions
        val rootExpression = expressionMapper.toExpression(params, searchyDescriptor.entityClass)

        // Validate the resulting expressions with the found Search Descriptor
        searchyValidationService.validate(rootExpression.toFieldExpressions(false), searchyDescriptor)

        // Find filtered entities from the Expressions
        val entities = entitySearchService.findAll(rootExpression, searchyDescriptor)

        // Convert found entities to DTOs
        return dtoConverterService.convert(entities, searchyDescriptor)
    }

}