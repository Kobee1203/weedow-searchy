package com.weedow.searchy.context

import com.weedow.searchy.query.querytype.PropertyInfos
import com.weedow.searchy.query.querytype.QEntity

/**
 * Class representing the Data Search Context used to create the queries according to the data access layer (JPA, MongoDB, ...).
 */
interface SearchyContext {

    /**
     * Annotation classes to determine the classes as an Entity
     */
    val entityAnnotations: List<Class<out Annotation>>

    /**
     * Annotation classes to determine the fields as a join
     */
    val joinAnnotations: List<Class<out Annotation>>

    /**
     * Returns the [Query Entity][QEntity] from the given [Entity class][entityClass].
     *
     * If the [Query Entity][QEntity] is not found, the [default] function is called.
     *
     * @param entityClass Entity class to be used for getting the related [Query Entity][QEntity]
     * @param default function called when the [Query Entity][QEntity] is not found. Throws an exception as default.
     * @return QEntity object
     */
    fun <E> get(
        entityClass: Class<E>,
        default: (entityClazz: Class<E>) -> QEntity<E> = { entityClazz -> throw IllegalArgumentException("Could not found the QEntity for $entityClazz") }
    ): QEntity<E>

    /**
     * Returns an list of [PropertyInfos] objects reflecting all the properties declared by the given Entity Class.
     * This includes inherited properties.
     * If this Entity Class represents a class with no declared fields, then this method returns a list of length 0.
     *
     * @param entityClass Entity Class to query
     * @return List of PropertyInfos
     */
    fun getAllPropertyInfos(entityClass: Class<*>): List<PropertyInfos>

    /**
     * Checks if the given class represents an Entity.
     *
     * @param clazz Class to check
     * @return `true` if the given class is an Entity Class, `false` instead
     */
    fun isEntity(clazz: Class<*>): Boolean

    /**
     * Checks if the given Annotation Class represents a Join Annotation.
     *
     * @param annotationClass Class to check
     * @return `true` if the given class represents a Join Annotation, `false` instead
     */
    fun isJoinAnnotation(annotationClass: Class<out Annotation>): Boolean

}
