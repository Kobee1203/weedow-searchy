package com.weedow.spring.data.search.join

import javax.persistence.criteria.JoinType

data class JoinInfo(
        val joinType: JoinType = JoinType.INNER,
        val fetched: Boolean = false
) {

}
