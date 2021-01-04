package com.weedow.spring.data.search.controller.reactive

import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.controller.AbstractDataSearchController
import com.weedow.spring.data.search.service.DataSearchService
import org.springframework.web.bind.annotation.RequestMethod


/**
 * Controller implementation for Web Reactive.
 *
 * Registers the request mapping for Spring Data Search in Reactive environment.
 *
 * @param dataSearchService [DataSearchService]
 * @param searchProperties [SearchProperties]
 * @param requestMappingHandlerMapping [org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping]
 */
class DataSearchReactiveController(
    dataSearchService: DataSearchService,
    searchProperties: SearchProperties,
    requestMappingHandlerMapping: org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
) : AbstractDataSearchController<org.springframework.web.reactive.result.method.RequestMappingInfo>(
    dataSearchService,
    searchProperties,
    requestMappingHandlerMapping::registerMapping
) {

    override fun createRequestMappingInfo(dataSearchPath: String): org.springframework.web.reactive.result.method.RequestMappingInfo {
        return org.springframework.web.reactive.result.method.RequestMappingInfo
            .paths(dataSearchPath)
            .methods(RequestMethod.GET)
            .build()
    }

}