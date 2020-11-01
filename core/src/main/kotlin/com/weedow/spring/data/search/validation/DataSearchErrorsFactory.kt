package com.weedow.spring.data.search.validation

/**
 * Interface to get a new [DataSearchErrors] instance.
 */
interface DataSearchErrorsFactory {

    /**
     * Returns a new instance of [DataSearchErrors].
     */
    fun getDataSearchErrors(): DataSearchErrors

}