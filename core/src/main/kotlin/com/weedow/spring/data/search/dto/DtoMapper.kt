package com.weedow.spring.data.search.dto

/**
 * Interface for a converter that can perform the conversion of an entity bean of type T to a DTO bean of type DTO.
 *
 * @param T Entity bean to be converted
 * @param DTO DTO bean produced from Entity bean
 */
interface DtoMapper<T, DTO> {

    /**
     * Maps the given source entity to a target DTO.
     *
     * @param source Entity bean to be converted
     *
     * @return DTO bean produced from Entity bean
     */
    fun map(source: T): DTO

}