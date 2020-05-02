package com.weedow.spring.data.search.dto

interface DtoMapper<T, R> {

    fun map(source: T): R

}