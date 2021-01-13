package com.weedow.searchy.controller.reactive

import com.weedow.searchy.config.SearchyProperties
import com.weedow.searchy.controller.AbstractSearchyController
import com.weedow.searchy.service.SearchyService
import org.springframework.web.bind.annotation.RequestMethod


/**
 * Controller implementation for Web Reactive.
 *
 * Registers the request mapping for Searchy in Reactive environment.
 *
 * @param searchyService [SearchyService]
 * @param searchyProperties [SearchyProperties]
 * @param requestMappingHandlerMapping [org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping]
 */
class SearchyReactiveController(
    searchyService: SearchyService,
    searchyProperties: SearchyProperties,
    requestMappingHandlerMapping: org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping
) : AbstractSearchyController<org.springframework.web.reactive.result.method.RequestMappingInfo>(
    searchyService,
    searchyProperties,
    requestMappingHandlerMapping::registerMapping
) {

    override fun createRequestMappingInfo(searchyPath: String): org.springframework.web.reactive.result.method.RequestMappingInfo {
        return org.springframework.web.reactive.result.method.RequestMappingInfo
            .paths(searchyPath)
            .methods(RequestMethod.GET)
            .build()
    }

}