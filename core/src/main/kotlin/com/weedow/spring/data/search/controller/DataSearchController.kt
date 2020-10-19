package com.weedow.spring.data.search.controller

import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.utils.klogger
import com.weedow.spring.data.search.validation.DataSearchValidationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*

/**
 * REST Controller to expose the Spring Data Search endpoint.
 *
 * The default base URI of the Spring data Search endpoint is '/search'.
 *
 * The base URI is completed with a Search Descriptor ID:
 * * If the Search Descriptor ID is found, the related [SearchDescriptor] is retrieved and used to perform the search.
 * * If the Search Descriptor ID is not found, An exception of type [SearchDescriptorNotFound] is thrown.
 */
@RestController
@RequestMapping("\${spring.data.search.base-path:${SearchProperties.DEFAULT_BASE_PATH}}")
class DataSearchController(
        private val searchDescriptorService: SearchDescriptorService,
        private val expressionMapper: ExpressionMapper,
        private val dataSearchService: DataSearchService,
        private val dataSearchValidationService: DataSearchValidationService
) {

    companion object {
        private val log by klogger()
    }

    @Value("\${spring.data.search.base-path:${SearchProperties.DEFAULT_BASE_PATH}}")
    private lateinit var basePath: String

    @GetMapping("/{searchDescriptorId}")
    fun search(@PathVariable searchDescriptorId: String, @RequestParam params: MultiValueMap<String, String>): ResponseEntity<List<*>> {
        if (log.isDebugEnabled) log.debug("Searching data from URI $basePath/$searchDescriptorId and following request parameters: $params")

        // Find Entity Search Descriptor
        val searchDescriptor = searchDescriptorService.getSearchDescriptor(searchDescriptorId)
                ?: throw SearchDescriptorNotFound(searchDescriptorId)

        val result = doSearch(params, searchDescriptor)

        return ResponseEntity.ok(result)
    }

    private fun <T> doSearch(params: MultiValueMap<String, String>, searchDescriptor: SearchDescriptor<T>): List<*> {
        // Mapping the given request parameters to the associated expressions
        val rootExpression = expressionMapper.toExpression(params, searchDescriptor.entityClass)

        // Validate the given parameters with the found Search Descriptor
        dataSearchValidationService.validate(rootExpression.toFieldExpressions(false), searchDescriptor)

        // Find entities according to field infos
        return dataSearchService.findAll(rootExpression, searchDescriptor)
    }

}