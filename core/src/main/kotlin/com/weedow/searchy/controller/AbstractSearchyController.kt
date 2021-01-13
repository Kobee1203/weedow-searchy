package com.weedow.searchy.controller

import com.weedow.searchy.config.SearchyProperties
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.exception.SearchyDescriptorNotFound
import com.weedow.searchy.service.SearchyService
import com.weedow.searchy.utils.klogger
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.lang.reflect.Method

/**
 * Convenient superclass for Searchy Controller implementations.
 *
 * It exposes the endpoint to search for data specified by [SearchyDescriptor]s.
 *
 * The [base path](SearchProperties.basePath) from [SearchyProperties] is completed with a Search Descriptor ID:
 * * If the Search Descriptor ID is found, the related [SearchyDescriptor] is retrieved and used to perform the search.
 * * If the Search Descriptor ID is not found, An exception of type [SearchyDescriptorNotFound] is thrown.
 *
 * @param searchyService [SearchyService]
 * @param searchyProperties [SearchyProperties]
 * @param mappingRegistrationFunction Function to register the Request Mapping for the current Controller
 */
abstract class AbstractSearchyController<M>(
    private val searchyService: SearchyService,
    private val searchyProperties: SearchyProperties,
    private val mappingRegistrationFunction: (mapping: M, AbstractSearchyController<M>, method: Method) -> Unit
) {

    companion object {
        private val log by klogger()
    }

    init {
        registerMapping("${searchyProperties.basePath}/{searchyDescriptorId}")

        if (log.isDebugEnabled) log.debug("Controller \"$javaClass\" initialized")
    }

    /**
     * Implement this method to create the Request Mapping Information object used to register the request mapping.
     */
    protected abstract fun createRequestMappingInfo(searchyPath: String): M

    private fun registerMapping(searchyPath: String) {
        val mapping = createRequestMappingInfo(searchyPath)

        val method = javaClass.getMethod("search", String::class.java, MultiValueMap::class.java)

        doRegisterMapping(mapping, this, method)
    }

    private fun doRegisterMapping(mapping: M, handler: AbstractSearchyController<M>, method: Method) {
        if (log.isDebugEnabled) log.debug("Register Mapping '$mapping' to ${method.toGenericString()}")
        mappingRegistrationFunction(mapping, handler, method)
    }

    /**
     * Method called by the registered the request mapping.
     *
     * The expected mapping HTTP request is: GET /${searchProperties.basePath}/{searchyDescriptorId}.
     *
     * @param searchyDescriptorId: Search Descriptor Identifier specified by a [SearchyDescriptor] and present in the URI
     * @param params Map of request parameters representing Entity fields used to search data
     */
    @ResponseBody
    fun search(@PathVariable searchyDescriptorId: String, @RequestParam params: MultiValueMap<String, String>): ResponseEntity<List<*>> {
        if (log.isDebugEnabled) log.debug("Searching data from URI ${searchyProperties.basePath}/$searchyDescriptorId and following request parameters: $params")

        val result = searchyService.search(searchyDescriptorId, params)

        return ResponseEntity.ok(result)
    }

}