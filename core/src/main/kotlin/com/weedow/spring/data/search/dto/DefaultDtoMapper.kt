package com.weedow.spring.data.search.dto

/**
 * Default [DtoMapper] implementation.
 *
 * There is no conversion: the given Entity bean is returned directly.
 */
class DefaultDtoMapper<T> : DtoMapper<T, T> {

    /**
     * Returns the given Entity bean directly
     *
     * @param source Entity bean
     * @return The same Entity bean as [source]
     */
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