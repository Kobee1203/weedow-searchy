package com.weedow.spring.data.search.dto

import com.weedow.spring.data.search.descriptor.SearchDescriptor

interface DtoConverterService<T, DTO> {

    fun convert(entities: List<T>, searchDescriptor: SearchDescriptor<T>): List<DTO>

}