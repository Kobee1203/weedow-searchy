package com.weedow.searchy.autoconfigure

import com.weedow.searchy.config.DelegatingSearchyConfiguration
import com.weedow.searchy.config.SearchyConfigurationSupport
import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.config.SearchyProperties
import com.weedow.searchy.controller.reactive.SearchyReactiveController
import com.weedow.searchy.controller.servlet.SearchyServletController
import com.weedow.searchy.service.SearchyService
import com.weedow.searchy.utils.klogger
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Auto-Configuration for Searchy.
 */
@Configuration
@ConditionalOnClass(SearchyConfigurer::class)
@ConditionalOnMissingBean(SearchyConfigurationSupport::class)
@EnableConfigurationProperties(SearchyProperties::class)
class SearchyAutoConfiguration {

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnBean(org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping::class)
    internal class EnableServletControllerAutoConfiguration {

        companion object {
            private val log by klogger()
        }

        init {
            log.info("Initializing Data Search Servlet Controller")
        }

        @Bean
        @ConditionalOnMissingBean
        fun searchyServletController(
            searchyService: SearchyService,
            searchyProperties: SearchyProperties,
            requestMappingHandlerMapping: org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping,
        ): SearchyServletController {
            return SearchyServletController(searchyService, searchyProperties, requestMappingHandlerMapping)
        }
    }

    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnBean(org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping::class)
    internal class EnableReactiveControllerAutoConfiguration {

        companion object {
            private val log by klogger()
        }

        init {
            log.info("Initializing Data Search Reactive Controller")
        }

        @Bean
        @ConditionalOnMissingBean
        fun searchyReactiveController(
            searchyService: SearchyService,
            searchyProperties: SearchyProperties,
            requestMappingHandlerMapping: org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping,
        ): SearchyReactiveController {
            return SearchyReactiveController(searchyService, searchyProperties, requestMappingHandlerMapping)
        }
    }

    @Configuration
    internal class EnableSearchyAutoConfiguration : DelegatingSearchyConfiguration() {

        companion object {
            private val log by klogger()
        }

        init {
            log.info("Initializing Data Search Configuration")
        }

    }

}
