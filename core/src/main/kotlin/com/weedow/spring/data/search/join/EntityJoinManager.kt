package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.descriptor.SearchDescriptor

interface EntityJoinManager {

    fun <T> computeEntityJoins(searchDescriptor: SearchDescriptor<T>): EntityJoins

}