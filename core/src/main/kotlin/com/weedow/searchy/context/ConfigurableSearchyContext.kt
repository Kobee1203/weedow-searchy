package com.weedow.searchy.context

import com.weedow.searchy.query.querytype.QEntity

/**
 * Configuration interface to be implemented by most if not all [SearchyContext] types.
 *
 * Consolidates the read-only operations exposed by [SearchyContext] and the mutating operations of this class to allow for
 * convenient ad-hoc addition and removal of Entity Class through.
 */
interface ConfigurableSearchyContext : SearchyContext {

    /**
     * Adds the given Entity Class in the current [SearchyContext] and returns the related [Query Entity][QEntity].
     *
     * @param entityClass Class to be added
     * @return [QEntity] related to the given Entity Class
     */
    fun <E> add(entityClass: Class<E>): QEntity<E>

}