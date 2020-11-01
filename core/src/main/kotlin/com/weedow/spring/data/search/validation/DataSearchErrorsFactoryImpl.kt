package com.weedow.spring.data.search.validation

/**
 * Default [DataSearchErrorsFactory] implementation.
 */
class DataSearchErrorsFactoryImpl : DataSearchErrorsFactory {

    override fun getDataSearchErrors(): DataSearchErrors {
        return DefaultDataSearchErrors()
    }

}