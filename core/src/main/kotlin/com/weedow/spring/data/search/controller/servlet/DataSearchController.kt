package com.weedow.spring.data.search.controller.servlet

import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.controller.AbstractDataSearchController
import com.weedow.spring.data.search.service.DataSearchService
import org.springframework.web.bind.annotation.RequestMethod


/**
 * Controller implementation for Web MVC.
 *
 * Registers the request mapping for Spring Data Search in Servlet environment.
 *
 * @param dataSearchService [DataSearchService]
 * @param searchProperties [SearchProperties]
 * @param requestMappingHandlerMapping [org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping]
 */
class DataSearchController(
    dataSearchService: DataSearchService,
    searchProperties: SearchProperties,
    requestMappingHandlerMapping: org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
) : AbstractDataSearchController<org.springframework.web.servlet.mvc.method.RequestMappingInfo>(
    dataSearchService,
    searchProperties,
    requestMappingHandlerMapping::registerMapping
) {

    override fun createRequestMappingInfo(dataSearchPath: String): org.springframework.web.servlet.mvc.method.RequestMappingInfo {
        return org.springframework.web.servlet.mvc.method.RequestMappingInfo
            .paths(dataSearchPath)
            .methods(RequestMethod.GET)
            .build()
    }

}