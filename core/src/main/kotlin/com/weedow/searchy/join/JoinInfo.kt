package com.weedow.searchy.join

import com.querydsl.core.JoinType
import com.weedow.searchy.join.JoinInfo.Companion.DEFAULT_FETCH_MODE
import com.weedow.searchy.join.JoinInfo.Companion.DEFAULT_JOIN_TYPE

/**
 * Value object with the information of a join.
 *
 * @param joinType [JoinType] object. Default is [DEFAULT_JOIN_TYPE]
 * @param fetched [Boolean] object. Default is [DEFAULT_FETCH_MODE]
 */
data class JoinInfo(
    val joinType: JoinType = DEFAULT_JOIN_TYPE,
    val fetched: Boolean = DEFAULT_FETCH_MODE
) {

    companion object {
        @JvmField
        val DEFAULT_JOIN_TYPE = JoinType.LEFTJOIN

        const val DEFAULT_FETCH_MODE = false
    }

}
