package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo

/**
 * Interface to specify join types for any fields having _join annotation_:
 *
 * The _join annotations_ detected by Spring Data Search are the following:
 * * javax.persistence.OneToOne
 * * javax.persistence.OneToMany
 * * javax.persistence.ManyToMany
 * * javax.persistence.ElementCollection
 * * javax.persistence.ManyToOne
 *
 * It is sometimes useful to optimize the number of SQL queries by specifying the data that you want to fetch during the first SQL query with the criteria.
 *
 * Here is an example:
 * ```
 * /**
 *  * Fetch all fields annotated with @ElementCollection
 *  **/
 * class MyEntityJoinHandler : EntityJoinHandler {
 *
 *   override fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean {
 *     return joinAnnotation instanceof ElementCollection;
 *   }
 *
 *   override fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo {
 *     return new JoinInfo(JoinType.LEFT, true);
 *   }
 * }
 * ```
 *
 * @see [FetchingAllEntityJoinHandler]
 * @see [FetchingEagerEntityJoinHandler]
 */
interface EntityJoinHandler<T> {

    /**
     * Checks if the given arguments are supported by this EntityJoinHandler.
     * * If the method returns `true`, the [handle] method will be called.
     * * If the method returns `false`, the [handle] method will not be called.
     *
     * @param entityClass Class of the Entity bean
     * @param fieldClass Class of the Field present on the Entity
     * @param fieldName Name of the Field
     * @param joinAnnotation Join Annotation present on the Entity Field
     * @return `true` if the EntityJoinHandler can [handle] join for these arguments
     */
    fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean

    /**
     * Handles the join type for the given arguments.
     *
     * @param entityClass Class of the Entity bean
     * @param fieldClass Class of the Field present on the Entity
     * @param fieldName Name of the Field
     * @param joinAnnotation Join Annotation present on the Entity Field
     * @return [JoinInfo]
     */
    fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo

}