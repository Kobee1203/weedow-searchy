package com.weedow.searchy.validation

/**
 * Default [SearchyErrors] implementation.
 */
class DefaultSearchyErrors : SearchyErrors {

    private val errors = mutableSetOf<SearchyError>()

    override fun getAllErrors(): Collection<SearchyError> {
        return errors
    }

    override fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

    override fun reject(errorCode: String, errorMessage: String, vararg arguments: Any) {
        errors.add(SearchyError(errorCode, errorMessage, arguments))
    }

}
