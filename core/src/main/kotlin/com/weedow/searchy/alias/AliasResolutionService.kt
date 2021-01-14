package com.weedow.searchy.alias

/**
 * A service interface for Alias resolution.
 *
 * Call [resolve] to retrieve the real field from the given alias.
 */
interface AliasResolutionService {

    /**
     * Resolve the given alias to the name of the field present in the given Class.
     *
     * If the field name related to the alias is not found, the method returns the given alias.
     *
     * @param parentClass Class where to find the field
     * @param alias alias to be resolved
     * @return String representing the field name resolved from the alias, or the alias instead.
     */
    fun resolve(parentClass: Class<*>, alias: String): String

}