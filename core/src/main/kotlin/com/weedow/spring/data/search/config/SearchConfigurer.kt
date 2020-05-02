package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import org.springframework.core.convert.converter.ConverterRegistry

interface SearchConfigurer {

    fun addSearchDescriptors(registry: SearchDescriptorRegistry) {}

    fun addConverters(registry: ConverterRegistry) {}

    fun addAliasResolvers(registry: AliasResolverRegistry) {}

}