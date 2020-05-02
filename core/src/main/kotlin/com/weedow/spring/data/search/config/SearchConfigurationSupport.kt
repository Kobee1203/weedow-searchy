package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.*
import com.weedow.spring.data.search.descriptor.*
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.core.convert.support.DefaultConversionService
import javax.annotation.PreDestroy
import javax.persistence.EntityManager

open class SearchConfigurationSupport {

    @Autowired
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
    protected open fun addAliasResolvers(registry: AliasResolverRegistry) {}

    @Bean
    open fun searchConversionService(converters: ObjectProvider<Converter<*, *>>): ConversionService {
        val conversionService: ConfigurableConversionService = DefaultConversionService()
        addConverters(conversionService)
        converters.orderedStream().forEach { conversionService.addConverter(it) }
        return conversionService
    }

    /**
     * Override this method to add custom [Converter]s.
     *
     * @param registry [ConverterRegistry]
     */
    protected open fun addConverters(registry: ConverterRegistry) {}
}