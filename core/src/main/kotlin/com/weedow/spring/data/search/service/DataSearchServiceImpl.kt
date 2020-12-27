package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.utils.klogger
import com.weedow.spring.data.search.validation.DataSearchValidationService
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

/**
 * Default [DataSearchService] implementation.
 *
 * Here are the steps made by this implementation:
 *   - Get the [SearchDescriptor] from the given SearchDescriptor Id
 *   - Convert the given map of parameters to [Expression][com.weedow.spring.data.search.expression.Expression]s
 *   - Validate the resulting expressions with the [SearchDescriptor]
 *   - Find filtered entities from the Expressions
 *   - Convert found entities to DTO
 *
 * This implementation uses transactions for any calls to methods of this class.
 * The transactions are `read-only` by default.
 *
 * @param searchDescriptorService [SearchDescriptorService]
 * @param expressionMapper [ExpressionMapper]
 * @param dataSearchValidationService [DataSearchValidationService]
 * @param entitySearchService [EntitySearchService]
 */
@Transactional(readOnly = true)
class DataSearchServiceImpl(
    private val searchDescriptorService: SearchDescriptorService,
    private val expressionMapper: ExpressionMapper,
    private val dataSearchValidationService: DataSearchValidationService,
    private val entitySearchService: EntitySearchService
) : DataSearchService {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized DataSearchService: {}", this::class.qualifiedName)
    }

    /**
     * {@inheritDoc}
     * @throws ValidationException when there are any validation errors
     */
    @Throws(SearchDescriptorNotFound::class, ValidationException::class)
    override fun search(searchDescriptorId: String, params: Map<String, List<String>>): List<*> {
        // Find Entity Search Descriptor
        val searchDescriptor = searchDescriptorService.getSearchDescriptor(searchDescriptorId)
            ?: throw SearchDescriptorNotFound(searchDescriptorId)

        return doSearch(params, searchDescriptor)
    }

    private fun <T> doSearch(params: Map<String, List<String>>, searchDescriptor: SearchDescriptor<T>): List<*> {
        // Mapping the given parameters to the associated expressions
        val rootExpression = expressionMapper.toExpression(params, searchDescriptor.entityClass)

        // Validate the resulting expressions with the found Search Descriptor
        dataSearchValidationService.validate(rootExpression.toFieldExpressions(false), searchDescriptor)

        // Find filtered entities from the Expressions
        val entities = entitySearchService.findAll(rootExpression, searchDescriptor)

        // Convert found entities to DTOs
        return convertToDto(entities, searchDescriptor)
    }

    private fun <T> convertToDto(entities: List<T>, searchDescriptor: SearchDescriptor<T>): List<*> {
        val dtoMapper = searchDescriptor.dtoMapper

        return entities.stream()
            .map { entity -> dtoMapper.map(entity) }
            .collect(Collectors.toList())
    }

}