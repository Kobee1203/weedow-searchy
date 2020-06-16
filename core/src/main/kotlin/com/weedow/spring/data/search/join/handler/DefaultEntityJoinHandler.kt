package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo

/**
 * Default [EntityJoinHandler] implementation.
 *
 * Accepts all join types and always returns the default values of [JoinInfo]: join type is [JoinInfo.DEFAULT_JOIN_TYPE] and fetch mode is [JoinInfo.DEFAULT_FETCH_MODE].
 */
class DefaultEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): Boolean {
        return true
    }

    override fun handle(entityClass: Class<*>, fieldClass: Class<*>, fieldName: String, joinAnnotation: Annotation): JoinInfo {
        return JoinInfo()
    }

}