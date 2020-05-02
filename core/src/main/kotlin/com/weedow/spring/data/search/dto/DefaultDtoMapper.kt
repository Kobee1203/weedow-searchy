package com.weedow.spring.data.search.dto

class DefaultDtoMapper<T> : DtoMapper<T, T> {

    override fun map(source: T): T {
        return source
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}