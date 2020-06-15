package com.weedow.spring.data.search.join

import javax.persistence.criteria.JoinType

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
