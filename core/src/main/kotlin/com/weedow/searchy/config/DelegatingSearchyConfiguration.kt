package com.weedow.searchy.config

import com.weedow.searchy.alias.AliasResolverRegistry
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry
import com.weedow.searchy.utils.klogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.ConverterRegistry

/**
 * A subclass of [SearchyConfigurationSupport] that detects and delegates to all beans of type [SearchyConfigurer] allowing them to customize the
 * configuration provided by [SearchyConfigurationSupport].
 */
@Configuration
class DelegatingSearchyConfiguration : SearchyConfigurationSupport() {

    companion object {
        private val log by klogger()
    }

    private val configurers = SearchyConfigurerComposite()

    /**
     * Injects automatically the given List of [SearchyConfigurer]s.
     */
    @Autowired(required = false)
    fun setConfigurers(configurers: List<SearchyConfigurer>) {
        log.debug("Adding search configurers: {}", configurers)
        this.configurers.addSearchyConfigurers(configurers)
    }

    override fun addSearchyDescriptors(registry: SearchyDescriptorRegistry) {
        super.addSearchyDescriptors(registry)
        configurers.addSearchyDescriptors(registry)
    }

    override fun addAliasResolvers(registry: AliasResolverRegistry) {
        super.addAliasResolvers(registry)
        configurers.addAliasResolvers(registry)
    }

    override fun addConverters(registry: ConverterRegistry) {
        super.addConverters(registry)
        configurers.addConverters(registry)
    }
}