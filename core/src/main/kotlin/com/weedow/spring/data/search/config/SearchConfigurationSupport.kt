package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.*
import com.weedow.spring.data.search.converter.StringToDateConverter
import com.weedow.spring.data.search.converter.StringToOffsetDateTimeConverter
import com.weedow.spring.data.search.descriptor.*
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.data.convert.Jsr310Converters
import javax.annotation.PreDestroy
import javax.persistence.EntityManager

/**
 * Main class providing the configuration of Spring Data Search.
 * An alternative more advanced option is to extend directly from this class and override methods as necessary, remembering to add [@Configuration][org.springframework.context.annotation.Configuration] to the subclass and [@Bean][Bean] to overridden [@Bean][Bean] methods.
 */
open class SearchConfigurationSupport {

    @Autowired(required = false)
    fun setEntityManager(entityManager: EntityManager) {
        JpaSpecificationExecutorFactory.init(entityManager)
    }

    @PreDestroy
    fun resetJpaSpecificationExecutorFactory() {
        JpaSpecificationExecutorFactory.reset()
    }

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
     */
    protected open fun addSearchDescriptors(registry: SearchDescriptorRegistry) {
        // Override this method to add custom SearchDescriptors.
    }

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
     */
    protected open fun addAliasResolvers(registry: AliasResolverRegistry) {
        // Override this method to add custom AliasResolvers.
    }

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
     */
    protected open fun addConverters(registry: ConverterRegistry) {
        // Override this method to add custom Converters.
    }
}