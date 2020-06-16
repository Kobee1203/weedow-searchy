package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.join.JoinInfo.Companion.DEFAULT_FETCH_MODE
import com.weedow.spring.data.search.join.JoinInfo.Companion.DEFAULT_JOIN_TYPE
import javax.persistence.criteria.JoinType

/**
 * Value object with the information of a join.
 *
 * The Join information are the following:
 * * [joinType] : [JoinType] object. Default is [DEFAULT_JOIN_TYPE]
 * * [fetched] : [Boolean] object. Default is [DEFAULT_FETCH_MODE]
 */
data class JoinInfo(
        val joinType: JoinType = DEFAULT_JOIN_TYPE,
        val fetched: Boolean = DEFAULT_FETCH_MODE
) {

    companion object {
        @JvmField
        val DEFAULT_JOIN_TYPE = JoinType.LEFT

        const val DEFAULT_FETCH_MODE = false
    }

}
