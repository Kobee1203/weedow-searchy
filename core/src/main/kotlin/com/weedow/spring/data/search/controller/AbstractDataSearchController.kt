package com.weedow.spring.data.search.controller

import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.utils.klogger
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
 *
 * @param dataSearchService [DataSearchService]
 * @param searchProperties [SearchProperties]
 * @param mappingRegistrationFunction Function to register the Request Mapping for the current Controller
 */
abstract class AbstractDataSearchController<M>(
    private val dataSearchService: DataSearchService,
    private val searchProperties: SearchProperties,
    private val mappingRegistrationFunction: (mapping: M, AbstractDataSearchController<M>, method: Method) -> Unit
) {

    companion object {
        private val log by klogger()
    }

    init {
        registerMapping("${searchProperties.basePath}/{searchDescriptorId}")

        if (log.isDebugEnabled) log.debug("Controller \"$javaClass\" initialized")
    }

    /**
     * Implement this method to create the Request Mapping Information object used to register the request mapping.
     */
    protected abstract fun createRequestMappingInfo(dataSearchPath: String): M

    private fun registerMapping(dataSearchPath: String) {
        val mapping = createRequestMappingInfo(dataSearchPath)

        val method = javaClass.getMethod("search", String::class.java, MultiValueMap::class.java)

        doRegisterMapping(mapping, this, method)
    }

    private fun doRegisterMapping(mapping: M, handler: AbstractDataSearchController<M>, method: Method) {
        if (log.isDebugEnabled) log.debug("Register Mapping '$mapping' to ${method.toGenericString()}")
        mappingRegistrationFunction(mapping, handler, method)
    }

    /**
     * Method called by the registered the request mapping.
     *
     * The expected mapping HTTP request is: GET /${searchProperties.basePath}/{searchDescriptorId}.
     *
     * @param searchDescriptorId: Search Descriptor Identifier specified by a [SearchDescriptor] and present in the URI
     * @param params Map of request parameters representing Entity fields used to search data
     */
    @ResponseBody
    fun search(@PathVariable searchDescriptorId: String, @RequestParam params: MultiValueMap<String, String>): ResponseEntity<List<*>> {
        if (log.isDebugEnabled) log.debug("Searching data from URI ${searchProperties.basePath}/$searchDescriptorId and following request parameters: $params")

        val result = dataSearchService.search(searchDescriptorId, params)

        return ResponseEntity.ok(result)
    }

}