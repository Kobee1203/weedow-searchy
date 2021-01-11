package com.weedow.searchy.descriptor

import com.weedow.searchy.dto.DtoMapper
import com.weedow.searchy.join.handler.EntityJoinHandler
import com.weedow.searchy.query.specification.SpecificationExecutor
import com.weedow.searchy.utils.TypeReference
import com.weedow.searchy.validation.SearchyValidator
import java.lang.reflect.ParameterizedType

/**
 * Class to construct a new [SearchyDescriptor] using the Builder pattern.
 *
 * @param entityClass Entity Class to be used for the [SearchyDescriptor]s created
 */
class SearchyDescriptorBuilder<T>(
    private val entityClass: Class<T>
) {

    private var id: String = this.entityClass.simpleName.decapitalize()
    private var validators: MutableList<SearchyValidator> = mutableListOf()
    private var dtoMapper: DtoMapper<T, *>? = null
    private var specificationExecutor: SpecificationExecutor<T>? = null
    private var entityJoinHandlers: MutableList<EntityJoinHandler> = mutableListOf()

    companion object {
        /**
         * Returns a new instance of [SearchyDescriptorBuilder].
         */
        inline fun <reified T> builder(): SearchyDescriptorBuilder<T> {
            val type = object : TypeReference<T>() {}.type
            val entityClass = if (type is ParameterizedType) type.actualTypeArguments[0] else type
            @Suppress("UNCHECKED_CAST")
            return SearchyDescriptorBuilder(entityClass as Class<T>)
        }
    }

    /**
     * Set the Search Descriptor ID.
     * If this method is not called, the default value is the Entity Name in lowercase, deduced by the given Entity Class.
     */
    fun id(id: String) = apply { this.id = id }

    /**
     * Set the validators to validate the query parameters.
     */
    fun validators(vararg validators: SearchyValidator) = apply { this.validators.addAll(validators) }

    /**
     * Set the [DTO Mapper][DtoMapper] to convert the Entities returned by the SQL queries to DTO object.
     * If this method is not called, the Entities are not converted and they are returned directly.
     */
    fun dtoMapper(dtoMapper: DtoMapper<T, *>) = apply { this.dtoMapper = dtoMapper }

    /**
     * Set a specific [SpecificationExecutor] to be used to search for entities.
     * If this method is not called, a default implementation of [SpecificationExecutor] is retrieved from instance of
     * [SpecificationExecutorFactory][com.weedow.searchy.query.specification.SpecificationExecutorFactory].
     *
     * @see com.weedow.searchy.query.specification.SpecificationExecutorFactory
     */
    fun specificationExecutor(specificationExecutor: SpecificationExecutor<T>) =
        apply { this.specificationExecutor = specificationExecutor }

    /**
     * Set the [Entity Join Handlers][EntityJoinHandler] to specify join types for any fields
     */
    fun entityJoinHandlers(vararg entityJoinHandlers: EntityJoinHandler) = apply { this.entityJoinHandlers.addAll(entityJoinHandlers) }

    /**
     * Builds a new [SearchyDescriptor] according to the specified options.
     */
    fun build(): SearchyDescriptor<T> {
        return DefaultSearchyDescriptor(
            id,
            entityClass,
            validators,
            dtoMapper,
            specificationExecutor,
            entityJoinHandlers
        )
    }

    /**
     * Default [SearchyDescriptor] implementation used by [SearchyDescriptorBuilder].
     */
    private data class DefaultSearchyDescriptor<T>(
        override val id: String,
        override val entityClass: Class<T>,
        override val validators: List<SearchyValidator>,
        override val dtoMapper: DtoMapper<T, *>?,
        override val specificationExecutor: SpecificationExecutor<T>?,
        override val entityJoinHandlers: List<EntityJoinHandler>,
    ) : SearchyDescriptor<T>
}