package com.weedow.searchy.utils

import java.io.Serializable

/**
 * Object to represent a `null` value.
 */
object NullValue : Serializable {

    /** Constant representing the 'null' value. */
    const val NULL_VALUE = "null"

    /** @suppress */
    override fun toString(): String {
        return javaClass.simpleName
    }

}