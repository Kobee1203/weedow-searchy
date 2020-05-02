package com.weedow.spring.data.search.utils

import java.io.Serializable

class NullValue private constructor() : Serializable {

    companion object {
        private const val serialVersionUID = 1L
        const val NULL_VALUE = "null"
        val INSTANCE = NullValue()
    }

    override fun equals(other: Any?): Boolean {
        return this === other || other == null
    }

    override fun hashCode(): Int {
        return NullValue::class.java.hashCode()
    }

    override fun toString(): String {
        return NULL_VALUE
    }

}