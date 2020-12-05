package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.exception.SearchDescriptorNotFound

/**
 * Service interface to search data related to the given [SearchDescriptor Id][SearchDescriptor] and filtered according to the given parameters Map.
 *
 * The parameters map contains a field path as a key and a value list associated with the field as value.
 *
 * @see SearchDescriptor
 */
interface DataSearchService {

    /**
     * Search data related to the given [searchDescriptorId] and filtered according to the given [parameters Map][params].
     *
     * @throws SearchDescriptorNotFound whether the [SearchDescriptor] is not found
     */
    @Throws(SearchDescriptorNotFound::class)
    fun search(searchDescriptorId: String, params: Map<String, List<String>>): List<*>

}