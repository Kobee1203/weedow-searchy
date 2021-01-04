package com.weedow.spring.data.search.dto

import com.weedow.spring.data.search.descriptor.SearchDescriptor

/**
 * Service interface to convert a list of entities of type [T] to a list of Data Transfer Objects (DTO) of type [DTO].
 *
 * @param T type of the Entities to be converted
 * @param DTO type of the DTOs produced from the Entities
 *
 * @see DtoMapper
 * @see DefaultDtoMapper
 */
interface DtoConverterService<T, DTO> {

    /**
     * Converts the given list of [entities] to a list of Data Transfer Objects (DTO).
     *
     * @param entities Entities to be converted
     * @param searchDescriptor [SearchDescriptor] that can define a specific [DtoMapper]. If it doesn't, a default conversion must be provided.
     * @return List of [DTO]s produced from the [entities]
     */
    fun convert(entities: List<T>, searchDescriptor: SearchDescriptor<T>): List<DTO>

}