package com.weedow.searchy.config

import com.weedow.searchy.alias.AliasResolverRegistry
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.util.CollectionUtils
import java.util.*

/**
 * A [SearchyConfigurer] that delegates to one or more others.
 */
class SearchyConfigurerComposite : SearchyConfigurer {

    private val delegates: MutableList<SearchyConfigurer> = ArrayList()

    /**
     * Add all detected delegated [SearchyConfigurer]s.
     */
    fun addSearchyConfigurers(configurers: List<SearchyConfigurer>) {
        if (!CollectionUtils.isEmpty(configurers)) {
            delegates.addAll(configurers)
        }
    }

    override fun addSearchyDescriptors(registry: SearchyDescriptorRegistry) {
        delegates.forEach { delegate -> delegate.addSearchyDescriptors(registry) }
    }

    override fun addAliasResolvers(registry: AliasResolverRegistry) {
        delegates.forEach { delegate -> delegate.addAliasResolvers(registry) }
    }

    override fun addConverters(registry: ConverterRegistry) {
        delegates.forEach { delegate -> delegate.addConverters(registry) }
    }
}