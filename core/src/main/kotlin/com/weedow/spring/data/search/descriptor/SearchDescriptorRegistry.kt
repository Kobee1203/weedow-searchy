package com.weedow.spring.data.search.descriptor

/**
 * Register the [SearchDescriptor]s.
 */
interface SearchDescriptorRegistry {

    /**
     * Adds a [SearchDescriptor] in the registry.
     *
     * @param searchDescriptor [SearchDescriptor] to be added
     */
    fun addSearchDescriptor(searchDescriptor: SearchDescriptor<*>)

}