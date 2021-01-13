package com.weedow.searchy.alias

import java.lang.reflect.Field

/**
 * An Alias resolver provides the list of aliases related to the given [Field] present in the given [Class].
 *
 * Here is an example where the AliasResolver implementation adds an alias for all fields present in Foo.Class ending with the 'Entity' suffix:
 * The alias value is the field name without the 'Entity' suffix.
 *
 * ```
 * class MyAliasResolver : AliasResolver {
 *
 *   companion object {
 *     private val SUFFIX = "Entity"
 *   }
 *
 *   override fun supports(entityClass: Class<*>, field: Field): Boolean {
 *     return entityClass.isAssignableFrom(Foo.class) && field.name.endsWith(SUFFIX)
 *   }
 *
 *   override fun resolve(entityClass: Class<*>, field: Field): List<String> {
 *     val fieldName = field.name
 *     return listOf(StringUtils.substringBefore(fieldName, SUFFIX))
 *   }
 * }
 * ```
 */
interface AliasResolver {

    /**
     * Check if the given [Class] and the given [Field] are supported by this AliasResolver.
     * * If the method returns `true`, the [resolve] method will be called.
     * * If the method returns `false`, the [resolve] method will not be called.
     *
     * @param entityClass The Class that the [AliasResolver] can being asked if it can [resolve] aliases
     * @param field The Field that the [AliasResolver] can being asked if it can [resolve] aliases
     * @return `true` if the AliasResolver can [resolve] aliases
     */
    fun supports(entityClass: Class<*>, field: Field): Boolean

    /**
     * Resolve the aliases for the given entityClass and given Field.
     *
     * @param entityClass The Class that the [AliasResolver] uses to resolve the related aliases
     * @param field The Field that the [AliasResolver] uses to resolve the related aliases
     * @return List of Strings representing the aliases
     */
    fun resolve(entityClass: Class<*>, field: Field): List<String>

}