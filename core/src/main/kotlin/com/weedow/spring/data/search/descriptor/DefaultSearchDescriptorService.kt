package com.weedow.spring.data.search.descriptor

/**
 * Default [SearchDescriptorService] implementation suitable for use in most environments.
 *
 * Indirectly implements [SearchDescriptorRegistry] as registration API through the [ConfigurableSearchDescriptorService] interface.
 */
class DefaultSearchDescriptorService : ConfigurableSearchDescriptorService {

    private val searchDescriptors = mutableMapOf<String, SearchDescriptor<*>>()

    override fun addSearchDescriptor(searchDescriptor: SearchDescriptor<*>) {
        searchDescriptors[searchDescriptor.id] = searchDescriptor
    }

    override fun getSearchDescriptor(id: String): SearchDescriptor<*>? {
        return searchDescriptors[id]
    }

}