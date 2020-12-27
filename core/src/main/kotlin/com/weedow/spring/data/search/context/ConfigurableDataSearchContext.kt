package com.weedow.spring.data.search.context

import com.weedow.spring.data.search.querydsl.querytype.QEntity

/**
 * Configuration interface to be implemented by most if not all [DataSearchContext] types.
 *
 * Consolidates the read-only operations exposed by [DataSearchContext] and the mutating operations of this class to allow for
 * convenient ad-hoc addition and removal of Entity Class through.
 */
interface ConfigurableDataSearchContext : DataSearchContext {

    /**
     * Adds the given Entity Class in the current [DataSearchContext] and returns the related [Query Entity][QEntity].
     *
     * @param entityClass Class to be added
     * @return [QEntity] related to the given Entity Class
     */
    fun <E> add(entityClass: Class<E>): QEntity<E>

}