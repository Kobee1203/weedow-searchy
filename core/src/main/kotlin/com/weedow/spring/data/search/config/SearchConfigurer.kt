package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import org.springframework.core.convert.converter.ConverterRegistry

interface SearchConfigurer {

    @JvmDefault
    fun addSearchDescriptors(registry: SearchDescriptorRegistry) {
        // Override this method to add custom SearchDescriptors.
    }

    @JvmDefault
    fun addAliasResolvers(registry: AliasResolverRegistry) {
        // Override this method to add custom AliasResolvers.
    }

    @JvmDefault
    fun addConverters(registry: ConverterRegistry) {
        // Override this method to add custom Converters.
    }

}