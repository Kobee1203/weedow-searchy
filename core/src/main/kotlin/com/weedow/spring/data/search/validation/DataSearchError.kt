package com.weedow.spring.data.search.validation

data class DataSearchError(
        val errorCode: String,
        val errorMessage: String,
        val arguments: Array<out Any>
) {

    constructor(errorCode: String, errorMessage: String) : this(errorCode, errorMessage, emptyArray<Any>())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataSearchError

        if (errorCode != other.errorCode) return false
        if (errorMessage != other.errorMessage) return false
        if (!arguments.contentEquals(other.arguments)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = errorCode.hashCode()
        result = 31 * result + errorMessage.hashCode()
        result = 31 * result + arguments.contentHashCode()
        return result
    }

}
