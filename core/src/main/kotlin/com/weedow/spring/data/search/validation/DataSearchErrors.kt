package com.weedow.spring.data.search.validation

/**
 * Interface to store and expose validation errors.
 */
interface DataSearchErrors {

    /**
     * Returns all stored [DataSearchError]s.
     */
    fun getAllErrors(): Collection<DataSearchError>

    /**
     * Returns if there are any errors.
     */
    fun hasErrors(): Boolean

    /**
     * Registers an validation error using the given error description.
     *
     * @param errorCode Error code
     * @param errorMessage Error message
     * @param arguments Error arguments for argument binding via MessageFormat. Can be `null`.e
     */
    fun reject(errorCode: String, errorMessage: String, vararg arguments: Any)

}
