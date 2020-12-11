package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.querydsl.QueryDslBuilder
import com.weedow.spring.data.search.querydsl.querytype.QPath
import javax.persistence.criteria.Path
import javax.persistence.criteria.Root

/**
 * Interface to get the joins or the [Path] relative to an Entity.
 */
interface EntityJoins {

    fun getQPath(fieldPath: String, queryDslBuilder: QueryDslBuilder<*>): QPath<*>

    /**
     * Returns the [Path] corresponding to given [field path][fieldPath] relative to the specified [Root].
     *
     * @param fieldPath path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
     * @param root [Root] object
     * @return [Path] representing the field found in the [field path][fieldPath]
     */
    fun <T> getPath(fieldPath: String, root: Root<T>): Path<*>

    /**
     * Returns every computed joins for the root Entity.
     *
     * @param filter allow to filter the joins and return a sub set according to the filter predicate
     * @return Map collection that contains join name as key and EntityJoin as value
     */
    fun getJoins(filter: (EntityJoin) -> Boolean = { true }): Map<String, EntityJoin>
}