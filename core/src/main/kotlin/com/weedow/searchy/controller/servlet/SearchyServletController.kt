package com.weedow.searchy.controller.servlet

import com.weedow.searchy.config.SearchyProperties
import com.weedow.searchy.controller.AbstractSearchyController
import com.weedow.searchy.service.SearchyService
import org.springframework.web.bind.annotation.RequestMethod


/**
 * Controller implementation for Web MVC.
 *
 * Registers the request mapping for Searchy in Servlet environment.
 *
 * @param searchyService [SearchyService]
 * @param searchyProperties [SearchyProperties]
 * @param requestMappingHandlerMapping [org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping]
 */
class SearchyServletController(
    searchyService: SearchyService,
    searchyProperties: SearchyProperties,
    requestMappingHandlerMapping: org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
) : AbstractSearchyController<org.springframework.web.servlet.mvc.method.RequestMappingInfo>(
    searchyService,
    searchyProperties,
    requestMappingHandlerMapping::registerMapping
) {

    override fun createRequestMappingInfo(searchyPath: String): org.springframework.web.servlet.mvc.method.RequestMappingInfo {
        return org.springframework.web.servlet.mvc.method.RequestMappingInfo
            .paths(searchyPath)
            .methods(RequestMethod.GET)
            .build()
    }

}