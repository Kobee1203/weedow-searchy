package com.weedow.spring.data.search.dto

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import java.util.stream.Collectors

/**
 * Default [DtoConverterService] implementation.
 *
 * @param defaultDtoMapper Default [DtoMapper] to be used when the given [SearchDescriptor] does not define a specific [DtoMapper]
 */
class DefaultDtoConverterServiceImpl<T, DTO>(
    private val defaultDtoMapper: DtoMapper<T, DTO>
) : DtoConverterService<T, DTO> {

    @Suppress("UNCHECKED_CAST")
    override fun convert(entities: List<T>, searchDescriptor: SearchDescriptor<T>): List<DTO> {
        val dtoMapper = searchDescriptor.dtoMapper ?: defaultDtoMapper

        return entities.stream()
            .map { entity -> dtoMapper.map(entity) }
            .collect(Collectors.toList()) as List<DTO>
    }

}