package com.weedow.spring.data.search.config

import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import com.weedow.spring.data.search.utils.klogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.ConverterRegistry

/**
 * A subclass of [SearchConfigurationSupport] that detects and delegates to all beans of type [SearchConfigurer] allowing them to customize the
 * configuration provided by [SearchConfigurationSupport].
 */
@Configuration
class DelegatingSearchConfiguration : SearchConfigurationSupport() {

    companion object {
        private val log by klogger()
    }

    private val configurers = SearchConfigurerComposite()

    /**
     * Injects automatically the given List of [SearchConfigurer]s.
     */
    @Autowired(required = false)
    fun setConfigurers(configurers: List<SearchConfigurer>) {
        log.debug("Adding search configurers: {}", configurers)
        this.configurers.addSearchConfigurers(configurers)
    }

    override fun addSearchDescriptors(registry: SearchDescriptorRegistry) {
        super.addSearchDescriptors(registry)
        configurers.addSearchDescriptors(registry)
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