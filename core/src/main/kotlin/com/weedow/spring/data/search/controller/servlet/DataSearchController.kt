package com.weedow.spring.data.search.controller.servlet

import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.controller.AbstractDataSearchController
import com.weedow.spring.data.search.descriptor.SearchDescriptorService
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.utils.klogger
import com.weedow.spring.data.search.validation.DataSearchValidationService
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping


/**
 * Controller implementation for Web MVC.
 *
 * Registers the request mapping for Spring Data Search in Servlet environment.
 */
class DataSearchController(
        searchDescriptorService: SearchDescriptorService,
        expressionMapper: ExpressionMapper,
        dataSearchService: DataSearchService,
        dataSearchValidationService: DataSearchValidationService,
        searchProperties: SearchProperties,
        requestMappingHandlerMapping: RequestMappingHandlerMapping
) : AbstractDataSearchController<RequestMappingInfo>(
        searchDescriptorService,
        expressionMapper,
        dataSearchService,
        dataSearchValidationService,
        searchProperties,
        requestMappingHandlerMapping::registerMapping
) {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Controller \"$javaClass\" initialized")
    }

    override fun createRequestMapping(dataSearchPath: String): RequestMappingInfo {
        return RequestMappingInfo
                .paths(dataSearchPath)
                .methods(RequestMethod.GET)
                .build()
    }

}