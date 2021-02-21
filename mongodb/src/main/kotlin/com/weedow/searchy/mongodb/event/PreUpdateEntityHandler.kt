package com.weedow.searchy.mongodb.event

/**
 * Interface to handle the [PreUpdate][com.weedow.searchy.mongodb.annotation.PreUpdate] annotation.
 */
interface PreUpdateEntityHandler {

    /**
     * Checks if the given Entity is supported by this PreUpdateEntityHandler.
     *
     * @param entity Entity to check
     * @return `true` if the PreUpdateEntityHandler supports the Entity
     */
    fun supports(entity: Any): Boolean

    /**
     * Handles the given supported Entity.
     *
     * @param entity Entity to handle
     */
    fun handle(entity: Any)

}