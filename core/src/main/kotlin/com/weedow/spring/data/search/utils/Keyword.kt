package com.weedow.spring.data.search.utils

import java.io.Serializable

/**
 * Keyword Object
 */
object Keyword : Serializable {

    /** Constant representing the 'CURRENT_DATE' value. */
    const val CURRENT_DATE = "CURRENT_DATE"

    /** Constant representing the 'CURRENT_TIME' value. */
    const val CURRENT_TIME = "CURRENT_TIME"

    /** Constant representing the 'CURRENT_DATE_TIME' value. */
    const val CURRENT_DATE_TIME = "CURRENT_DATE_TIME"

    /** @suppress */
    override fun toString(): String {
        return javaClass.simpleName
    }

}
