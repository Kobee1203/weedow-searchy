package com.weedow.spring.data.search.descriptor

class DefaultSearchDescriptorService : ConfigurableSearchDescriptorService {

    private val searchDescriptors = mutableMapOf<String, SearchDescriptor<*>>()

    override fun addSearchDescriptor(searchDescriptor: SearchDescriptor<*>) {
        searchDescriptors[searchDescriptor.id] = searchDescriptor
    }

    override fun getSearchDescriptor(id: String): SearchDescriptor<*>? {
        return searchDescriptors[id]
    }

}