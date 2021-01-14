package com.weedow.searchy.descriptor

/**
 * Register the [SearchyDescriptor]s.
 */
interface SearchyDescriptorRegistry {

    /**
     * Adds a [SearchyDescriptor] in the registry.
     *
     * @param searchyDescriptor [SearchyDescriptor] to be added
     */
    fun addSearchyDescriptor(searchyDescriptor: SearchyDescriptor<*>)

}