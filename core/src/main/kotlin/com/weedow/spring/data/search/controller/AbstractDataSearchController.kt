package com.weedow.spring.data.search.controller

import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.utils.klogger
import com.weedow.spring.data.search.validation.DataSearchValidationService
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.lang.reflect.Method

/**
 * Convenient superclass for Spring Data Search Controller implementations.
 *
 * It exposes the endpoint to search for data specified by [SearchDescriptor]s.
 *
 * The [base path](SearchProperties.basePath) from [SearchProperties] is completed with a Search Descriptor ID:
 * * If the Search Descriptor ID is found, the related [SearchDescriptor] is retrieved and used to perform the search.
 * * If the Search Descriptor ID is not found, An exception of type [SearchDescriptorNotFound] is thrown.
 */
abstract class AbstractDataSearchController<M>(
        private val searchDescriptorService: SearchDescriptorService,
        private val expressionMapper: ExpressionMapper,
        private val dataSearchService: DataSearchService,
        private val dataSearchValidationService: DataSearchValidationService,
        private val searchProperties: SearchProperties,
        private val mappingRegistrationFunction: (mapping: M, AbstractDataSearchController<M>, method: Method) -> Unit
) {

    companion object {
        private val log by klogger()
    }

    init {
        registerMapping("${searchProperties.basePath}/{searchDescriptorId}")
    }

    protected abstract fun createRequestMapping(dataSearchPath: String): M

    private fun registerMapping(dataSearchPath: String) {
        val mapping = createRequestMapping(dataSearchPath)

        val method = javaClass.getMethod("search", String::class.java, MultiValueMap::class.java)

        doRegisterMapping(mapping, this, method)
    }

    private fun doRegisterMapping(mapping: M, handler: AbstractDataSearchController<M>, method: Method) {
        if (log.isDebugEnabled) log.debug("Register Mapping '$mapping' to ${method.toGenericString()}")
        mappingRegistrationFunction(mapping, handler, method)
    }

    // Get Mapping: /${searchProperties.basePath}/{searchDescriptorId}
    @ResponseBody
    fun search(@PathVariable searchDescriptorId: String, @RequestParam params: MultiValueMap<String, String>): ResponseEntity<List<*>> {
        if (log.isDebugEnabled) log.debug("Searching data from URI ${searchProperties.basePath}/$searchDescriptorId and following request parameters: $params")

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