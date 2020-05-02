package com.weedow.spring.data.search.alias

interface AliasResolverRegistry {

    fun addAliasResolver(aliasResolver: AliasResolver)

}