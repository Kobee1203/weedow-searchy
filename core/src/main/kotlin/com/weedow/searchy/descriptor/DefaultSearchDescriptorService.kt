package com.weedow.searchy.descriptor

/**
 * Default [SearchyDescriptorService] implementation suitable for use in most environments.
 *
 * Indirectly implements [SearchyDescriptorRegistry] as registration API through the [ConfigurableSearchyDescriptorService] interface.
 */
class DefaultSearchyDescriptorService : ConfigurableSearchyDescriptorService {

    private val searchyDescriptors = mutableMapOf<String, SearchyDescriptor<*>>()

    override fun addSearchyDescriptor(searchyDescriptor: SearchyDescriptor<*>) {
        searchyDescriptors[searchyDescriptor.id] = searchyDescriptor
    }

    override fun getSearchyDescriptor(id: String): SearchyDescriptor<*>? {
        return searchyDescriptors[id]
    }

}