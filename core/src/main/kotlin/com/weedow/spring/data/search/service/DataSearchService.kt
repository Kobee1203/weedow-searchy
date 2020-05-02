package com.weedow.spring.data.search.service

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.field.FieldInfo

interface DataSearchService {

    fun <T> findAll(fieldInfos: List<FieldInfo>, searchDescriptor: SearchDescriptor<T>): List<T>

}