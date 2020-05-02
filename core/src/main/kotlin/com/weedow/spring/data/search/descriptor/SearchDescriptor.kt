package com.weedow.spring.data.search.descriptor

import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.join.EntityJoinHandler
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface SearchDescriptor<T> {

    /**
     * String representing an identifier to find this Descriptor.
     *
     * @return Id as String
     */
    val id: String

    /**
     * Entity Class to be searched.
     *
     * @return Class representing an Entity
     */
    val entityClass: Class<T>

    /**
     * Return the mapper to convert the entity to a specific DTO.
     *
     * By default, the method returns a mapper that just returns the given entity.
     *
     * @param <R> represents the DTO type
     * @return DTO Object
     */
    val dtoMapper: DtoMapper<T, *>
        get() = DefaultDtoMapper()

    /**
     * Return the [JpaSpecificationExecutor] to search the entities.
     *
     * @return JpaSpecificationExecutor
     * @see JpaSpecificationExecutor
     */
    val jpaSpecificationExecutor: JpaSpecificationExecutor<T>

    /**
     * Return list of [EntityJoinHandler]s to handle the entity joins
     */
    val entityJoinHandlers: List<EntityJoinHandler<T>>
        get() = listOf()
}