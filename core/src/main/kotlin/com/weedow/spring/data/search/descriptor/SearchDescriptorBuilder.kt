package com.weedow.spring.data.search.descriptor

import com.weedow.spring.data.search.config.JpaSpecificationExecutorFactory
import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import com.weedow.spring.data.search.utils.TypeReference
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.lang.reflect.ParameterizedType

/**
 * Class to construct a new [SearchDescriptor] using the Builder pattern.
 */
class SearchDescriptorBuilder<T>(
        private val entityClass: Class<T>
) {

    private var id: String = this.entityClass.simpleName.decapitalize()
    private var dtoMapper: DtoMapper<T, *> = DefaultDtoMapper()
    private lateinit var specificationExecutor: JpaSpecificationExecutor<T>
    private var entityJoinHandlers: MutableList<EntityJoinHandler<T>> = mutableListOf()

    init {
        if (JpaSpecificationExecutorFactory.isInitialized()) {
            this.specificationExecutor = JpaSpecificationExecutorFactory.getJpaSpecificationExecutor(entityClass)
        }
    }

    companion object {
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
     * Set the [DTO Mapper][DtoMapper] to convert the Entities returned by the SQL queries to DTO object.
     * If this method is not called, the Entities are not converted and they are returned directly.
     */
    fun dtoMapper(dtoMapper: DtoMapper<T, *>) = apply { this.dtoMapper = dtoMapper }

    /**
     * Set a specific [JpaSpecificationExecutor] to be used to search for entities.
     * If this method is not called, a default implementation of [JpaSpecificationExecutor] is retrieved from [JpaSpecificationExecutorFactory].
     */
    fun jpaSpecificationExecutor(jpaSpecificationExecutor: JpaSpecificationExecutor<T>) = apply { this.specificationExecutor = jpaSpecificationExecutor }

    /**
     * Set the [Entity Join Handlers][EntityJoinHandler] to specify join types for any fields having [Join Annotation][com.weedow.spring.data.search.utils.EntityUtils.JOIN_ANNOTATIONS]_
     */
    fun entityJoinHandlers(vararg entityJoinHandlers: EntityJoinHandler<T>) = apply { this.entityJoinHandlers.addAll(entityJoinHandlers) }

    /**
     * Builds a new [SearchDescriptor] according to the specified options.
     */
    fun build(): SearchDescriptor<T> {
        require(::specificationExecutor.isInitialized) {
            "JPA SpecificationExecutor is required. JpaSpecificationExecutorFactory is not initialized with an EntityManager. Use 'jpaSpecificationExecutor' method."
        }

        return DefaultSearchDescriptor(id, entityClass, dtoMapper, specificationExecutor, entityJoinHandlers)
    }

    /**
     * Default [SearchDescriptor] implementation used by [SearchDescriptorBuilder].
     */
    private data class DefaultSearchDescriptor<T> internal constructor(
            override val id: String,
            override val entityClass: Class<T>,
            override val dtoMapper: DtoMapper<T, *>,
            override val jpaSpecificationExecutor: JpaSpecificationExecutor<T>,
            override val entityJoinHandlers: List<EntityJoinHandler<T>>
    ) : SearchDescriptor<T>
}
