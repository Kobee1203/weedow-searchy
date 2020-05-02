package com.weedow.spring.data.search.descriptor

interface SearchDescriptorRegistry {

    fun addSearchDescriptor(searchDescriptor: SearchDescriptor<*>)

}