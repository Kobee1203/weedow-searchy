package com.weedow.spring.data.search.descriptor

/**
 * A service interface for [Search Descriptor][SearchDescriptor].
 */
interface SearchDescriptorService {

    /**
     * Returns the [SearchDescriptor] corresponding to the given Search Descriptor ID.
     *
     * @param id Search Descriptor ID
     * @return [SearchDescriptor] or null if not found
     */
    fun getSearchDescriptor(id: String): SearchDescriptor<*>?

}