package com.weedow.searchy.config

import com.weedow.searchy.alias.AliasResolverRegistry
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry
import org.springframework.core.convert.converter.ConverterRegistry

/**
 * Interface that defines callback methods to customize the configuration for Searchy.
 *
 * Implement this interface to customize the Searchy configuration.
 */
interface SearchyConfigurer {

    /**
     * Adds [com.weedow.searchy.descriptor.SearchyDescriptor]s to expose automatically search endpoints for Entities.
     */
    @JvmDefault
    fun addSearchyDescriptors(registry: SearchyDescriptorRegistry) {
        // Override this method to add custom SearchyDescriptors.
    }

    /**
     * Adds [com.weedow.searchy.alias.AliasResolver]s to register alias for any fields and so use these aliases in queries instead of the
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