package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.*
import com.weedow.spring.data.search.context.ConfigurableDataSearchContext
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.converter.StringToDateConverter
import com.weedow.spring.data.search.converter.StringToOffsetDateTimeConverter
import com.weedow.spring.data.search.descriptor.*
import com.weedow.spring.data.search.dto.DefaultDtoConverterServiceImpl
import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.dto.DtoConverterService
import com.weedow.spring.data.search.dto.DtoMapper
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
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
import com.weedow.spring.data.search.query.specification.SpecificationService
import com.weedow.spring.data.search.query.specification.SpecificationServiceImpl
import com.weedow.spring.data.search.service.DataSearchService
import com.weedow.spring.data.search.service.DataSearchServiceImpl
import com.weedow.spring.data.search.service.EntitySearchService
import com.weedow.spring.data.search.service.EntitySearchServiceImpl
import com.weedow.spring.data.search.validation.DataSearchErrorsFactory
import com.weedow.spring.data.search.validation.DataSearchErrorsFactoryImpl
import com.weedow.spring.data.search.validation.DataSearchValidationService
import com.weedow.spring.data.search.validation.DataSearchValidationServiceImpl
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.domain.EntityScanner
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.convert.Jsr310Converters

/**
 * Main class providing the configuration of Spring Data Search.
 *
 * An alternative more advanced option is to extend directly from this class and override methods as necessary, remembering to add
 * [@Configuration][org.springframework.context.annotation.Configuration] to the subclass and [@Bean][Bean] to overridden [@Bean][Bean] methods.
 */
open class SearchConfigurationSupport {

    @EventListener
    fun handleContextRefreshEvent(cre: ContextRefreshedEvent) {
        val dataSearchContext = cre.applicationContext.getBean(DataSearchContext::class.java)

        if (dataSearchContext is ConfigurableDataSearchContext) {
            val entityClasses = EntityScanner(cre.applicationContext).scan(*dataSearchContext.entityAnnotations.toTypedArray())
            entityClasses.forEach { entityClass ->
                dataSearchContext.add(entityClass)
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    open fun fieldPathResolver(searchAliasResolutionService: AliasResolutionService): FieldPathResolver {
        return FieldPathResolverImpl(searchAliasResolutionService)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun fieldInfoResolver(fieldPathResolver: FieldPathResolver, searchConversionService: ConversionService): ExpressionResolver {
        return ExpressionResolverImpl(fieldPathResolver, searchConversionService)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun expressionMapper(expressionResolver: ExpressionResolver, expressionParser: ExpressionParser): ExpressionMapper {
        return ExpressionMapperImpl(expressionResolver, expressionParser)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun expressionParserVisitorFactory(expressionResolver: ExpressionResolver): ExpressionParserVisitorFactory {
        return ExpressionParserVisitorFactoryImpl(expressionResolver)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun expressionParser(expressionParserVisitorFactory: ExpressionParserVisitorFactory): ExpressionParser {
        return ExpressionParserImpl(expressionParserVisitorFactory)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun <T, DTO> dataSearchService(
        searchDescriptorService: SearchDescriptorService,
        expressionMapper: ExpressionMapper,
        dataSearchValidationService: DataSearchValidationService,
        entitySearchService: EntitySearchService,
        dtoConverterService: DtoConverterService<T, DTO>
    ): DataSearchService {
        return DataSearchServiceImpl(searchDescriptorService, expressionMapper, dataSearchValidationService, entitySearchService, dtoConverterService)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun dataSearchValidationService(dataSearchErrorsFactory: DataSearchErrorsFactory): DataSearchValidationService {
        return DataSearchValidationServiceImpl(dataSearchErrorsFactory)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun dataSearchErrorsFactory(): DataSearchErrorsFactory {
        return DataSearchErrorsFactoryImpl()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun entitySearchService(
        specificationService: SpecificationService,
        specificationExecutorFactory: SpecificationExecutorFactory
    ): EntitySearchService {
        return EntitySearchServiceImpl(specificationService, specificationExecutorFactory)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun <T> defaultDtoMapper(): DtoMapper<T, T> {
        return DefaultDtoMapper()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun <T, DTO> dtoConverterService(
        defaultDtoMapper: DtoMapper<T, DTO>
    ): DtoConverterService<T, DTO> {
        return DefaultDtoConverterServiceImpl(defaultDtoMapper)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun specificationService(entityJoinManager: EntityJoinManager): SpecificationService {
        return SpecificationServiceImpl(entityJoinManager)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun entityJoinManager(dataSearchContext: DataSearchContext): EntityJoinManager {
        return EntityJoinManagerImpl(dataSearchContext)
    }

    /**
     * Return a [SearchDescriptorService] initialized with the given [SearchDescriptor]s declared as Beans.
     *
     * See [addSearchDescriptors] as an alternative to overriding this method.
     */
    @Bean
    open fun searchDescriptorService(searchDescriptors: ObjectProvider<SearchDescriptor<*>>): SearchDescriptorService {
        val searchDescriptorService: ConfigurableSearchDescriptorService = DefaultSearchDescriptorService()
        addSearchDescriptors(searchDescriptorService)
        searchDescriptors.orderedStream().forEach { searchDescriptorService.addSearchDescriptor(it) }
        return searchDescriptorService
    }

    /**
     * Override this method to add custom [SearchDescriptor]s.
     *
     * @param registry [SearchDescriptorRegistry]
     * @see searchDescriptorService
     */
    protected open fun addSearchDescriptors(registry: SearchDescriptorRegistry) {
        // Override this method to add custom SearchDescriptors.
    }

    /**
     * Return a [AliasResolutionService] initialized with the given [AliasResolver]s declared as Beans.
     *
     * See [addAliasResolvers] as an alternative to overriding this method.
     */
    @Bean
    open fun searchAliasResolutionService(aliasResolvers: ObjectProvider<AliasResolver>): AliasResolutionService {
        val aliasResolutionService: ConfigurableAliasResolutionService = DefaultAliasResolutionService()
        addAliasResolvers(aliasResolutionService)
        aliasResolvers.orderedStream().forEach { aliasResolutionService.addAliasResolver(it) }
        return aliasResolutionService
    }

    /**
     * Override this method to add custom [AliasResolver]s.
     *
     * @param registry [AliasResolverRegistry]
     * @see searchAliasResolutionService
     */
    protected open fun addAliasResolvers(registry: AliasResolverRegistry) {
        // Override this method to add custom AliasResolvers.
    }

    /**
     * Return a [ConversionService] initialized with the given [Converter]s declared as Beans.
     *
     * See [addConverters] as an alternative to overriding this method.
     */
    @Bean
    open fun searchConversionService(converters: ObjectProvider<Converter<*, *>>): ConversionService {
        val conversionService: ConfigurableConversionService = DefaultConversionService()
        addDefaultConverters(conversionService)
        addConverters(conversionService)
        converters.orderedStream().forEach { conversionService.addConverter(it) }
        return conversionService
    }

    private fun addDefaultConverters(registry: ConverterRegistry) {
        Jsr310Converters.getConvertersToRegister().forEach { registry.addConverter(it) }
        registry.addConverter(StringToOffsetDateTimeConverter())
        registry.addConverter(StringToDateConverter())
    }

    /**
     * Override this method to add custom [Converter]s.
     *
     * @param registry [ConverterRegistry]
     * @see searchConversionService
     */
    protected open fun addConverters(registry: ConverterRegistry) {
        // Override this method to add custom Converters.
    }
}