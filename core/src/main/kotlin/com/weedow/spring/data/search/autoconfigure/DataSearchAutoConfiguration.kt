package com.weedow.spring.data.search.autoconfigure

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.weedow.spring.data.search.alias.AliasResolutionService
import com.weedow.spring.data.search.config.DelegatingSearchConfiguration
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.expression.ExpressionMapperImpl
import com.weedow.spring.data.search.field.FieldPathResolver
import com.weedow.spring.data.search.field.FieldPathResolverImpl
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.join.EntityJoinManagerImpl
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.service.DataSearchServiceImpl
import com.weedow.spring.data.search.specification.JpaSpecificationService
import com.weedow.spring.data.search.specification.JpaSpecificationServiceImpl
import com.weedow.spring.data.search.utils.klogger
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.ConversionService

@Configuration
@ConditionalOnClass(SearchConfigurer::class)
class DataSearchAutoConfiguration : DelegatingSearchConfiguration() {

    @Configuration
    @ComponentScan("com.weedow.spring.data.search.controller")
    internal class EnableControllerAutoConfiguration

    @Configuration
    internal class EnableDataSearchAutoConfiguration {

        companion object {
            private val log by klogger()
        }

        init {
            log.info("Initializing Data Search Configuration")
        }

        @Bean
        @ConditionalOnMissingBean
        fun fieldPathResolver(searchAliasResolutionService: AliasResolutionService): FieldPathResolver {
            return FieldPathResolverImpl(searchAliasResolutionService)
        }

        @Bean
        @ConditionalOnMissingBean
        fun expressionMapper(fieldPathResolver: FieldPathResolver, searchConversionService: ConversionService): ExpressionMapper {
            return ExpressionMapperImpl(fieldPathResolver, searchConversionService)
        }

        @Bean
        @ConditionalOnMissingBean
        fun dataSearchService(jpaSpecificationService: JpaSpecificationService, entityJoinManager: EntityJoinManager): DataSearchService {
            return DataSearchServiceImpl(jpaSpecificationService, entityJoinManager)
        }

        @Bean
        @ConditionalOnMissingBean
        fun entityJoinManager(): EntityJoinManager {
            return EntityJoinManagerImpl()
        }

        @Bean
        @ConditionalOnMissingBean
        fun jpaSpecificationService(): JpaSpecificationService {
            return JpaSpecificationServiceImpl()
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
