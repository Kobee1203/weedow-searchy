package com.weedow.spring.data.search.descriptor

import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutor
import com.weedow.spring.data.search.utils.TypeReference
import com.weedow.spring.data.search.validation.DataSearchValidator
import java.lang.reflect.ParameterizedType

/**
 * Class to construct a new [SearchDescriptor] using the Builder pattern.
 *
 * @param entityClass Entity Class to be used for the [SearchDescriptor]s created
 */
class SearchDescriptorBuilder<T>(
    private val entityClass: Class<T>
) {

    private var id: String = this.entityClass.simpleName.decapitalize()
    private var validators: MutableList<DataSearchValidator> = mutableListOf()
    private var dtoMapper: DtoMapper<T, *> = DefaultDtoMapper()
    private var queryDslSpecificationExecutor: QueryDslSpecificationExecutor<T>? = null
    private var entityJoinHandlers: MutableList<EntityJoinHandler> = mutableListOf()

    companion object {
        /**
         * Returns a new instance of [SearchDescriptorBuilder].
         */
        inline fun <reified T> builder(): SearchDescriptorBuilder<T> {
            val type = object : TypeReference<T>() {}.type
            val entityClass = if (type is ParameterizedType) type.actualTypeArguments[0] else type
            @Suppress("UNCHECKED_CAST")
            return SearchDescriptorBuilder(entityClass as Class<T>)
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
    fun validators(vararg validators: DataSearchValidator) = apply { this.validators.addAll(validators) }

    /**
     * Set the [DTO Mapper][DtoMapper] to convert the Entities returned by the SQL queries to DTO object.
     * If this method is not called, the Entities are not converted and they are returned directly.
     */
    fun dtoMapper(dtoMapper: DtoMapper<T, *>) = apply { this.dtoMapper = dtoMapper }

    /**
     * Set a specific [QueryDslSpecificationExecutor] to be used to search for entities.
     * If this method is not called, a default implementation of [QueryDslSpecificationExecutor] is retrieved from instance of
     * [QueryDslSpecificationExecutorFactory][com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactory].
     *
     * @see com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactory
     */
    fun queryDslSpecificationExecutor(queryDslSpecificationExecutor: QueryDslSpecificationExecutor<T>) =
        apply { this.queryDslSpecificationExecutor = queryDslSpecificationExecutor }

    /**
     * Set the [Entity Join Handlers][EntityJoinHandler] to specify join types for any fields
     */
    fun entityJoinHandlers(vararg entityJoinHandlers: EntityJoinHandler) = apply { this.entityJoinHandlers.addAll(entityJoinHandlers) }

    /**
     * Builds a new [SearchDescriptor] according to the specified options.
     */
    fun build(): SearchDescriptor<T> {
        return DefaultSearchDescriptor(
            id,
            entityClass,
            validators,
            dtoMapper,
            queryDslSpecificationExecutor,
            entityJoinHandlers
        )
    }

    /**
     * Default [SearchDescriptor] implementation used by [SearchDescriptorBuilder].
     */
    private data class DefaultSearchDescriptor<T>(
        override val id: String,
        override val entityClass: Class<T>,
        override val validators: List<DataSearchValidator>,
        override val dtoMapper: DtoMapper<T, *>,
        override val queryDslSpecificationExecutor: QueryDslSpecificationExecutor<T>?,
        override val entityJoinHandlers: List<EntityJoinHandler>,
    ) : SearchDescriptor<T>
}