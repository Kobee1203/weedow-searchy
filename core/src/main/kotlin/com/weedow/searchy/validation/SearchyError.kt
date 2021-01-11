package com.weedow.searchy.validation

/**
 * Data Class representing a validation error.
 *
 * @param errorCode Error code
 * @param errorMessage Error message
 * @param arguments Error arguments for argument binding via MessageFormat. Can be `null`.
 */
data class SearchyError(
    val errorCode: String,
    val errorMessage: String,
    val arguments: Array<out Any>
) {

    /**
     * Secondary constructor.
     *
     * @param errorCode Error code
     * @param errorMessage Error message
     */
    constructor(errorCode: String, errorMessage: String) : this(errorCode, errorMessage, emptyArray<Any>())

    /** @suppress */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchyError

        if (errorCode != other.errorCode) return false
        if (errorMessage != other.errorMessage) return false
        if (!arguments.contentEquals(other.arguments)) return false

        return true
    }

    /** @suppress */
    override fun hashCode(): Int {
        var result = errorCode.hashCode()
        result = 31 * result + errorMessage.hashCode()
        result = 31 * result + arguments.contentHashCode()
        return result
    }

}
