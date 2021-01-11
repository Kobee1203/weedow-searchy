package com.weedow.searchy.descriptor

/**
 * A service interface for [Search Descriptor][SearchyDescriptor].
 */
interface SearchyDescriptorService {

    /**
     * Returns the [SearchyDescriptor] corresponding to the given Search Descriptor ID.
     *
     * @param id Search Descriptor ID
     * @return [SearchyDescriptor] or null if not found
     */
    fun getSearchyDescriptor(id: String): SearchyDescriptor<*>?

}