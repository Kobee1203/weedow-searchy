package com.weedow.searchy.dto

import com.weedow.searchy.descriptor.SearchyDescriptor
import java.util.stream.Collectors

/**
 * Default [DtoConverterService] implementation.
 *
 * @param defaultDtoMapper Default [DtoMapper] to be used when the given [SearchyDescriptor] does not define a specific [DtoMapper]
 */
class DefaultDtoConverterServiceImpl<T, DTO>(
    private val defaultDtoMapper: DtoMapper<T, DTO>
) : DtoConverterService<T, DTO> {

    @Suppress("UNCHECKED_CAST")
    override fun convert(entities: List<T>, searchyDescriptor: SearchyDescriptor<T>): List<DTO> {
        val dtoMapper = searchyDescriptor.dtoMapper ?: defaultDtoMapper

        return entities.stream()
            .map { entity -> dtoMapper.map(entity) }
            .collect(Collectors.toList()) as List<DTO>
    }

}