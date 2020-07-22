package com.weedow.spring.data.search.autoconfigure

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.weedow.spring.data.search.alias.AliasResolutionService
import com.weedow.spring.data.search.config.DelegatingSearchConfiguration
import com.weedow.spring.data.search.config.SearchConfigurationSupport
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.config.SearchProperties
import com.weedow.spring.data.search.expression.ExpressionMapper
import com.weedow.spring.data.search.expression.ExpressionMapperImpl
import com.weedow.spring.data.search.expression.ExpressionResolver
import com.weedow.spring.data.search.expression.ExpressionResolverImpl
import com.weedow.spring.data.search.expression.parser.ExpressionParser
import com.weedow.spring.data.search.expression.parser.ExpressionParserImpl
import com.weedow.spring.data.search.expression.parser.ExpressionParserVisitorFactory
import com.weedow.spring.data.search.expression.parser.ExpressionParserVisitorFactoryImpl
import com.weedow.spring.data.search.fieldpath.FieldPathResolver
import com.weedow.spring.data.search.fieldpath.FieldPathResolverImpl
import com.weedow.spring.data.search.join.EntityJoinManager
import com.weedow.spring.data.search.join.EntityJoinManagerImpl
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.service.DataSearchServiceImpl
import com.weedow.spring.data.search.specification.JpaSpecificationService
import com.weedow.spring.data.search.specification.JpaSpecificationServiceImpl
import com.weedow.spring.data.search.utils.klogger
import com.weedow.spring.data.search.validation.DataSearchErrorsFactory
import com.weedow.spring.data.search.validation.DataSearchErrorsFactoryImpl
import com.weedow.spring.data.search.validation.DataSearchValidationService
import com.weedow.spring.data.search.validation.DataSearchValidationServiceImpl
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.ConversionService

/**
 * Auto-Configuration for Spring Data Search.
 */
@Configuration
@ConditionalOnClass(SearchConfigurer::class)
@ConditionalOnMissingBean(SearchConfigurationSupport::class)
@EnableConfigurationProperties(SearchProperties::class)
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
        fun fieldInfoResolver(fieldPathResolver: FieldPathResolver, searchConversionService: ConversionService): ExpressionResolver {
            return ExpressionResolverImpl(fieldPathResolver, searchConversionService)
        }

        @Bean
        @ConditionalOnMissingBean
        fun expressionMapper(expressionResolver: ExpressionResolver, expressionParser: ExpressionParser): ExpressionMapper {
            return ExpressionMapperImpl(expressionResolver, expressionParser)
        }

        @Bean
        @ConditionalOnMissingBean
        fun expressionParserVisitorFactory(expressionResolver: ExpressionResolver): ExpressionParserVisitorFactory {
            return ExpressionParserVisitorFactoryImpl(expressionResolver)
        }

        @Bean
        @ConditionalOnMissingBean
        fun expressionParser(expressionParserVisitorFactory: ExpressionParserVisitorFactory): ExpressionParser {
            return ExpressionParserImpl(expressionParserVisitorFactory)
        }

        @Bean
        @ConditionalOnMissingBean
        fun dataSearchService(jpaSpecificationService: JpaSpecificationService, entityJoinManager: EntityJoinManager): DataSearchService {
            return DataSearchServiceImpl(jpaSpecificationService, entityJoinManager)
        }

        @Bean
        @ConditionalOnMissingBean
        fun dataSearchValidationService(dataSearchErrorsFactory: DataSearchErrorsFactory): DataSearchValidationService {
            return DataSearchValidationServiceImpl(dataSearchErrorsFactory)
        }

        @Bean
        @ConditionalOnMissingBean
        fun dataSearchErrorsFactory(): DataSearchErrorsFactory {
            return DataSearchErrorsFactoryImpl()
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
