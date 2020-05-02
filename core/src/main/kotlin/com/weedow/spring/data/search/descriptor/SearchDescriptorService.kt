package com.weedow.spring.data.search.descriptor

interface SearchDescriptorService {

    fun getSearchDescriptor(id: String): SearchDescriptor<*>?

}