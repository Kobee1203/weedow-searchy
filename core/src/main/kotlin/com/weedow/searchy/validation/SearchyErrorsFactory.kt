package com.weedow.searchy.validation

/**
 * Interface to get a new [SearchyErrors] instance.
 */
interface SearchyErrorsFactory {

    /**
     * Returns a new instance of [SearchyErrors].
     */
    fun getSearchyErrors(): SearchyErrors

}