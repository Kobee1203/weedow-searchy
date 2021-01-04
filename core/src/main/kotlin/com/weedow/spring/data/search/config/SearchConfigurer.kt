package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import org.springframework.core.convert.converter.ConverterRegistry

/**
 * Interface that defines callback methods to customize the configuration for Spring Data Search.
 *
 * Implement this interface to customize the Spring Data Search configuration.
 */
interface SearchConfigurer {

    /**
     * Adds [com.weedow.spring.data.search.descriptor.SearchDescriptor]s to expose automatically search endpoints for Entities.
     */
    @JvmDefault
    fun addSearchDescriptors(registry: SearchDescriptorRegistry) {
        // Override this method to add custom SearchDescriptors.
    }

    /**
     * Adds [com.weedow.spring.data.search.alias.AliasResolver]s to register alias for any fields and so use these aliases in queries instead of the
     * real field name.
     */
    @JvmDefault
    fun addAliasResolvers(registry: AliasResolverRegistry) {
        // Override this method to add custom AliasResolvers.
    }

    /**
     * Adds [org.springframework.core.convert.converter.Converter]s to be used while converting query parameter values from String to the correct type
     * expected by the related field.
     */
    @JvmDefault
    fun addConverters(registry: ConverterRegistry) {
        // Override this method to add custom Converters.
    }

}