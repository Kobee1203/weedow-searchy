package com.weedow.spring.data.search.join

import javax.persistence.criteria.Path
import javax.persistence.criteria.Root

interface EntityJoins {

    fun <T> getPath(fieldPath: String, root: Root<T>): Path<*>

    /**
     * Returns every computed joins for the root Entity.
     *
     * @param filter allow to filter the joins and return a sub set according to the filter predicate
     * @return Map collection that contains join name as key and EntityJoin as value
     */
    fun getJoins(filter: (EntityJoin) -> Boolean = { true }): Map<String, EntityJoin>
}