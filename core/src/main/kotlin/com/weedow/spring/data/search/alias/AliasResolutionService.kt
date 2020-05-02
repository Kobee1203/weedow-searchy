package com.weedow.spring.data.search.alias

interface AliasResolutionService {

    fun resolve(parentClass: Class<*>, alias: String): String

}