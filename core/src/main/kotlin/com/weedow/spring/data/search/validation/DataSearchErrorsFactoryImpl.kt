package com.weedow.spring.data.search.validation

class DataSearchErrorsFactoryImpl : DataSearchErrorsFactory {

    override fun getDataSearchErrors(): DataSearchErrors {
        return DefaultDataSearchErrors()
    }

}