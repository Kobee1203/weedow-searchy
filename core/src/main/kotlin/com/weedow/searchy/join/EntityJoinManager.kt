package com.weedow.searchy.join

import com.weedow.searchy.descriptor.SearchyDescriptor

/**
 * Interface to manage the Entity joins.
 */
interface EntityJoinManager {

    /**
     * Computes the joins for the Entity defined in the given [SearchyDescriptor].
     *
     * The [EntityJoinHandlers][com.weedow.searchy.join.handler.EntityJoinHandler] defined in the [SearchyDescriptor] are used to check
     * the join type for each related field annotated with a Join Annotation.
     *
     * @param searchyDescriptor [SearchyDescriptor] with the Entity Class and the [EntityJoinHandlers][com.weedow.searchy.join.handler.EntityJoinHandler].
     * @return [EntityJoins] object
     */
    fun <T> computeEntityJoins(searchyDescriptor: SearchyDescriptor<T>): EntityJoins

}