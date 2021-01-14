package com.weedow.searchy.config

import com.weedow.searchy.alias.*
import com.weedow.searchy.context.ConfigurableSearchyContext
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.converter.StringToDateConverter
import com.weedow.searchy.converter.StringToOffsetDateTimeConverter
import com.weedow.searchy.descriptor.*
import com.weedow.searchy.dto.DefaultDtoConverterServiceImpl
import com.weedow.searchy.dto.DefaultDtoMapper
import com.weedow.searchy.dto.DtoConverterService
import com.weedow.searchy.dto.DtoMapper
import com.weedow.searchy.expression.ExpressionMapper
import com.weedow.searchy.expression.ExpressionMapperImpl
import com.weedow.searchy.expression.ExpressionResolver
import com.weedow.searchy.expression.ExpressionResolverImpl
import com.weedow.searchy.expression.parser.ExpressionParser
import com.weedow.searchy.expression.parser.ExpressionParserImpl
import com.weedow.searchy.expression.parser.ExpressionParserVisitorFactory
import com.weedow.searchy.expression.parser.ExpressionParserVisitorFactoryImpl
import com.weedow.searchy.fieldpath.FieldPathResolver
import com.weedow.searchy.fieldpath.FieldPathResolverImpl
import com.weedow.searchy.join.EntityJoinManager
import com.weedow.searchy.join.EntityJoinManagerImpl
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationService
import com.weedow.searchy.query.specification.SpecificationServiceImpl
import com.weedow.searchy.service.SearchyService
import com.weedow.searchy.service.SearchyServiceImpl
import com.weedow.searchy.service.EntitySearchService
import com.weedow.searchy.service.EntitySearchServiceImpl
import com.weedow.searchy.validation.SearchyErrorsFactory
import com.weedow.searchy.validation.SearchyErrorsFactoryImpl
import com.weedow.searchy.validation.SearchyValidationService
import com.weedow.searchy.validation.SearchyValidationServiceImpl
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
 * Main class providing the configuration of Searchy.
 *
 * An alternative more advanced option is to extend directly from this class and override methods as necessary, remembering to add
 * [@Configuration][org.springframework.context.annotation.Configuration] to the subclass and [@Bean][Bean] to overridden [@Bean][Bean] methods.
 */
open class SearchyConfigurationSupport {

    @EventListener
    fun handleContextRefreshEvent(cre: ContextRefreshedEvent) {
        val searchyContext = cre.applicationContext.getBean(SearchyContext::class.java)

        if (searchyContext is ConfigurableSearchyContext) {
            val entityClasses = EntityScanner(cre.applicationContext).scan(*searchyContext.entityAnnotations.toTypedArray())
            entityClasses.forEach { entityClass ->
                searchyContext.add(entityClass)
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
    open fun <T, DTO> searchyService(
        searchyDescriptorService: SearchyDescriptorService,
        expressionMapper: ExpressionMapper,
        searchyValidationService: SearchyValidationService,
        entitySearchService: EntitySearchService,
        dtoConverterService: DtoConverterService<T, DTO>
    ): SearchyService {
        return SearchyServiceImpl(searchyDescriptorService, expressionMapper, searchyValidationService, entitySearchService, dtoConverterService)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun searchyValidationService(searchyErrorsFactory: SearchyErrorsFactory): SearchyValidationService {
        return SearchyValidationServiceImpl(searchyErrorsFactory)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun searchyErrorsFactory(): SearchyErrorsFactory {
        return SearchyErrorsFactoryImpl()
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
    open fun entityJoinManager(searchyContext: SearchyContext): EntityJoinManager {
        return EntityJoinManagerImpl(searchyContext)
    }

    /**
     * Return a [SearchyDescriptorService] initialized with the given [SearchyDescriptor]s declared as Beans.
     *
     * See [addSearchyDescriptors] as an alternative to overriding this method.
     */
    @Bean
    open fun searchyDescriptorService(searchyDescriptors: ObjectProvider<SearchyDescriptor<*>>): SearchyDescriptorService {
        val searchyDescriptorService: ConfigurableSearchyDescriptorService = DefaultSearchyDescriptorService()
        addSearchyDescriptors(searchyDescriptorService)
        searchyDescriptors.orderedStream().forEach { searchyDescriptorService.addSearchyDescriptor(it) }
        return searchyDescriptorService
    }

    /**
     * Override this method to add custom [SearchyDescriptor]s.
     *
     * @param registry [SearchyDescriptorRegistry]
     * @see searchyDescriptorService
     */
    protected open fun addSearchyDescriptors(registry: SearchyDescriptorRegistry) {
        // Override this method to add custom SearchyDescriptors.
    }

    /**
     * Return a [AliasResolutionService] initialized with the given [AliasResolver]s declared as Beans.
     *
     * See [addAliasResolvers] as an alternative to overriding this method.
     */
    @Bean
    open fun searchAliasResolutionService(aliasResolvers: ObjectProvider<AliasResolver>): AliasResolutionService {
        val aliasResolutionService: ConfigurableAliasResolutionService =
            DefaultAliasResolutionService()
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