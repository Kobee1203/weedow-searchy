package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.descriptor.SearchDescriptor

/**
 * Interface to manage the Entity joins.
 */
interface EntityJoinManager {

    /**
     * Computes the joins for the Entity defined in the given [SearchDescriptor].
     *
     * The [EntityJoinHandlers][com.weedow.spring.data.search.join.handler.EntityJoinHandler] defined in the [SearchDescriptor] are used to check the join type for each related field annotated with a Join Annotation.
     *
     * @param searchDescriptor [SearchDescriptor] with the Entity Class and the [EntityJoinHandlers][com.weedow.spring.data.search.join.handler.EntityJoinHandler].
     * @return [EntityJoins] object
     */
    fun <T> computeEntityJoins(searchDescriptor: SearchDescriptor<T>): EntityJoins

}