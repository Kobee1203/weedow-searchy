package com.weedow.searchy.mongodb.event

/**
 * Interface to handle the [PrePersist][com.weedow.searchy.mongodb.annotation.PrePersist] annotation.
 */
interface PrePersistEntityHandler {

    /**
     * Checks if the given Entity is supported by this PrePersistEntityHandler.
     *
     * @param entity Entity to check
     * @return `true` if the PrePersistEntityHandler supports the Entity
     */
    fun supports(entity: Any): Boolean

    /**
     * Handles the given supported Entity.
     *
     * @param entity Entity to handle
     */
    fun handle(entity: Any)

}