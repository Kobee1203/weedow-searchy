package com.weedow.spring.data.search.dto

interface DtoMapper<S, T> {

    fun map(source: S): T

}