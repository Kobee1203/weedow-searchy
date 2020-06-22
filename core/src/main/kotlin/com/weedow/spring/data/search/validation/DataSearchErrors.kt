package com.weedow.spring.data.search.validation

interface DataSearchErrors {

    fun getAllErrors(): List<DataSearchError>

    fun hasErrors(): Boolean

    fun reject(errorCode: String, errorMessage: String)

}
