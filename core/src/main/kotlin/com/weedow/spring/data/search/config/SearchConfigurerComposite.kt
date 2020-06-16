package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.util.CollectionUtils
import java.util.*

/**
 * A [SearchConfigurer] that delegates to one or more others.
 */
class SearchConfigurerComposite : SearchConfigurer {

    private val delegates: MutableList<SearchConfigurer> = ArrayList()

    fun addSearchConfigurers(configurers: List<SearchConfigurer>) {
        if (!CollectionUtils.isEmpty(configurers)) {
            delegates.addAll(configurers)
        }
    }

    override fun addSearchDescriptors(registry: SearchDescriptorRegistry) {
        delegates.forEach { delegate -> delegate.addSearchDescriptors(registry) }
    }

    override fun addAliasResolvers(registry: AliasResolverRegistry) {
        delegates.forEach { delegate -> delegate.addAliasResolvers(registry) }
    }

    override fun addConverters(registry: ConverterRegistry) {
        delegates.forEach { delegate -> delegate.addConverters(registry) }
    }
}