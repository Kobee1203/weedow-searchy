package com.weedow.spring.data.search.validation

class DefaultDataSearchErrors : DataSearchErrors {

    private val errors = mutableSetOf<DataSearchError>()

    override fun getAllErrors(): Collection<DataSearchError> {
        return errors
    }

    override fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

    override fun reject(errorCode: String, errorMessage: String, vararg arguments: Any) {
        errors.add(DataSearchError(errorCode, errorMessage, arguments))
    }

}
