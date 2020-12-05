package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.config.DelegatingSearchConfiguration
import com.weedow.spring.data.search.config.SearchConfigurationSupport
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.controller.reactive.DataSearchReactiveController
import com.weedow.spring.data.search.controller.servlet.DataSearchController
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.utils.klogger
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Auto-Configuration for Spring Data Search.
 */
@Configuration
@ConditionalOnClass(SearchConfigurer::class)
@ConditionalOnMissingBean(SearchConfigurationSupport::class)
@EnableConfigurationProperties(SearchProperties::class)
class DataSearchAutoConfiguration {

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
        fun servletDataSearchController(
                dataSearchService: DataSearchService,
                searchProperties: SearchProperties,
                requestMappingHandlerMapping: org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping,
        ): DataSearchController {
            return DataSearchController(dataSearchService, searchProperties, requestMappingHandlerMapping)
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
        fun reactiveDataSearchController(
                dataSearchService: DataSearchService,
                searchProperties: SearchProperties,
                requestMappingHandlerMapping: org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping,
        ): DataSearchReactiveController {
            return DataSearchReactiveController(dataSearchService, searchProperties, requestMappingHandlerMapping)
        }
    }

    @Configuration
    internal class EnableDataSearchAutoConfiguration : DelegatingSearchConfiguration() {

        companion object {
            private val log by klogger()
        }

        init {
            log.info("Initializing Data Search Configuration")
        }

    }

}
