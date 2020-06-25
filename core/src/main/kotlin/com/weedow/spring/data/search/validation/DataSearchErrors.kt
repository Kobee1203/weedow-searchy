package com.weedow.spring.data.search.validation

interface DataSearchErrors {

    fun getAllErrors(): Collection<DataSearchError>

    fun hasErrors(): Boolean

    fun reject(errorCode: String, errorMessage: String, vararg arguments: Any)

}
