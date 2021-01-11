package com.weedow.searchy.alias

/**
 * Register the [AliasResolver]s.
 */
interface AliasResolverRegistry {

    /**
     * Adds an [AliasResolver] in the registry.
     *
     * @param aliasResolver [AliasResolver] to be added
     */
    fun addAliasResolver(aliasResolver: AliasResolver)

}