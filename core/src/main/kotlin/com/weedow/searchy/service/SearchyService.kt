package com.weedow.searchy.service

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.exception.SearchyDescriptorNotFound

/**
 * Service interface to search data related to the given [SearchyDescriptor Id][SearchyDescriptor] and filtered according to the given parameters Map.
 *
 * The parameters map contains a field path as a key and a value list associated with the field as value.
 *
 * @see SearchyDescriptor
 */
interface SearchyService {

    /**
     * Search data related to the given [searchyDescriptorId] and filtered according to the given [parameters Map][params].
     *
     * @throws SearchyDescriptorNotFound whether the [SearchyDescriptor] is not found
     */
    @Throws(SearchyDescriptorNotFound::class)
    fun search(searchyDescriptorId: String, params: Map<String, List<String>>): List<*>

}