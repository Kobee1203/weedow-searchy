package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.query.QueryBuilder
import com.weedow.spring.data.search.query.querytype.QEntityRoot
import com.weedow.spring.data.search.query.querytype.QPath

/**
 * Interface to get the computed joins or the [QPath] relative to an Entity.
 */
interface EntityJoins {

    /**
     * Returns the [QPath] corresponding to the given [field path][fieldPath] relative to the specified [QEntityRoot].
     *
     * @param fieldPath path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
     * @param qEntityRoot [QEntityRoot] object
     * @param queryBuilder [QueryBuilder] instance to find or create joins
     * @return [QPath] representing the field found from the [field path][fieldPath]
     */
    fun <T> getQPath(fieldPath: String, qEntityRoot: QEntityRoot<T>, queryBuilder: QueryBuilder<T>): QPath<*>

    /**
     * Returns every computed joins for the root Entity.
     *
     * @param filter allow to filter the joins and return a sub set according to the filter predicate
     * @return Map collection that contains join name as key and EntityJoin as value
     */
    fun getJoins(filter: (EntityJoin) -> Boolean = { true }): Map<String, EntityJoin>
}