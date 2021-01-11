package com.weedow.searchy.join.handler

import com.weedow.searchy.join.JoinInfo
import com.weedow.searchy.query.querytype.PropertyInfos

/**
 * Default [EntityJoinHandler] implementation.
 *
 * Accepts all join types and always returns the default values of [JoinInfo]:
 * * join type is [JoinInfo.DEFAULT_JOIN_TYPE]
 * * fetch mode is [JoinInfo.DEFAULT_FETCH_MODE].
 */
class DefaultEntityJoinHandler : EntityJoinHandler {

    override fun supports(propertyInfos: PropertyInfos): Boolean {
        return true
    }

    override fun handle(propertyInfos: PropertyInfos): JoinInfo {
        return JoinInfo()
    }

}