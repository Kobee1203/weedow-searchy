package com.weedow.spring.data.search.utils

import java.io.Serializable

/**
 * Object to represent a `null` value.
 */
object NullValue : Serializable {

    const val NULL_VALUE = "null"

    override fun toString(): String {
        return javaClass.simpleName
    }

}