package com.weedow.spring.data.search.validation

class DefaultDataSearchErrors : DataSearchErrors {

    private val errors = mutableListOf<DataSearchError>()

    override fun getAllErrors(): List<DataSearchError> {
        return errors
    }

    override fun hasErrors(): Boolean {
        return errors.isNotEmpty()
    }

    override fun reject(errorCode: String, errorMessage: String) {
        errors.add(DataSearchError(errorCode, errorMessage))
    }

}
