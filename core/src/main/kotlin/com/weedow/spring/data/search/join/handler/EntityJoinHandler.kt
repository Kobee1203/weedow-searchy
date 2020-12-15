package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo
import com.weedow.spring.data.search.querydsl.querytype.PropertyInfos

/**
 * Interface to specify join types for any fields having _join annotation_:
 *
 * It is sometimes useful to optimize the number of queries by specifying the data that you want to fetch during the first query with the criteria.
 *
 * Here is an example:
 * ```
 * /**
 *  * Fetch all fields annotated with @ElementCollection
 *  **/
 * class MyEntityJoinHandler : EntityJoinHandler {
 *
 *   override fun supports(entityClass: Class<*>, propertyInfos: PropertyInfos): Boolean {
 *     return propertyInfos.annotations.any { it is ElementCollection }
 *   }
 *
 *   override fun handle(entityClass: Class<*>, propertyInfos: PropertyInfos): JoinInfo {
 *     return new JoinInfo(JoinType.LEFT, true);
 *   }
 * }
 * ```
 *
 * @see FetchingAllEntityJoinHandler
 * @see JpaFetchingEagerEntityJoinHandler
 */
interface EntityJoinHandler {

    /**
     * Checks if the given arguments are supported by this EntityJoinHandler.
     * * If the method returns `true`, the [handle] method will be called.
     * * If the method returns `false`, the [handle] method will not be called.
     *
     * @param propertyInfos Information about an Entity property
     * @return `true` if the EntityJoinHandler can [handle] join for these arguments
     * @see PropertyInfos
     */
    fun supports(propertyInfos: PropertyInfos): Boolean

    /**
     * Handles the join type for the given arguments.
     *
     * @param propertyInfos Information about an Entity property
     * @return [JoinInfo]
     * @see PropertyInfos
     */
    fun handle(propertyInfos: PropertyInfos): JoinInfo

}