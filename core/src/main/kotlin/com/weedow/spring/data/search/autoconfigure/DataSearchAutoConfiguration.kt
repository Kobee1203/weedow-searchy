package com.weedow.spring.data.search.autoconfigure

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
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
                requestMappingHandlerMapping: org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping): DataSearchController {
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
                requestMappingHandlerMapping: org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping): DataSearchReactiveController {
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

    @Configuration
    @ConditionalOnClass(Hibernate5Module::class)
    internal class DataSearchHibernateSerializationAutoConfiguration {

        /**
         * Add-on module for Jackson JSON processor which handles Hibernate (http://www.hibernate.org/) datatypes; and specifically aspects of lazy-loading.
         *
         * Can be useful when we use [com.weedow.spring.data.search.dto.DefaultDtoMapper] while serializing the result of entities to JSON, and manage lazy-loading automatically.
         *
         * To prevent the Jackson infinite recursion problem with bidirectional relationships, please use one of the following solutions:
         * - [@JsonManagedReference][com.fasterxml.jackson.annotation.JsonManagedReference] and [@JsonBackReference][com.fasterxml.jackson.annotation.JsonBackReference]
         * - [@JsonIdentityInfo][com.fasterxml.jackson.annotation.JsonIdentityInfo]
         * - [@JsonIgnoreProperties][com.fasterxml.jackson.annotation.JsonIgnoreProperties]
         * - [@JsonIgnore][com.fasterxml.jackson.annotation.JsonIgnore]
         *
         * @see com.weedow.spring.data.search.dto.DefaultDtoMapper
         * @see com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature
         * @see <a href="https://github.com/FasterXML/jackson-datatype-hibernate">jackson-datatype-hibernate Github</a>
         * @see <a href="https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations">Jackson Annotations</a>
         */
        @Bean
        @ConditionalOnMissingBean
        fun hibernateModule(): Module {
            return Hibernate5Module()
                    .enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING)
                    .enable(Hibernate5Module.Feature.WRITE_MISSING_ENTITIES_AS_NULL)
        }
    }
}
