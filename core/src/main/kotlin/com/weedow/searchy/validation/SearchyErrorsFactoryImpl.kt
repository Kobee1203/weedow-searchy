package com.weedow.searchy.validation

/**
 * Default [SearchyErrorsFactory] implementation.
 */
class SearchyErrorsFactoryImpl : SearchyErrorsFactory {

    override fun getSearchyErrors(): SearchyErrors {
        return DefaultSearchyErrors()
    }

}