package com.weedow.spring.data.search.join.handler

import com.weedow.spring.data.search.join.JoinInfo
import com.weedow.spring.data.search.querydsl.querytype.PropertyInfos

/**
 * Default [EntityJoinHandler] implementation.
 *
 * Accepts all join types and always returns the default values of [JoinInfo]: join type is [JoinInfo.DEFAULT_JOIN_TYPE] and fetch mode is [JoinInfo.DEFAULT_FETCH_MODE].
 */
class DefaultEntityJoinHandler<T> : EntityJoinHandler<T> {

    override fun supports(propertyInfos: PropertyInfos): Boolean {
        return true
    }

    override fun handle(propertyInfos: PropertyInfos): JoinInfo {
        return JoinInfo()
    }

}