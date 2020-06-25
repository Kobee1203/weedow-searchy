package com.weedow.spring.data.search.fieldpath

/**
 * Interface to resolve the field path information from the given [field path][fieldPath]
 */
interface FieldPathResolver {

    /**
     * Resolves the field path information from the given [field path][fieldPath] relative to the specified [root class][rootClass].
     *
     * @param rootClass root class
     * @param fieldPath path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
     */
    fun resolveFieldPath(rootClass: Class<*>, fieldPath: String): FieldPathInfo

}