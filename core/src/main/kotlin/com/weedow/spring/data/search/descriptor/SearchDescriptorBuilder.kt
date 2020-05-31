package com.weedow.spring.data.search.descriptor

import com.weedow.spring.data.search.config.JpaSpecificationExecutorFactory
import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import com.weedow.spring.data.search.utils.TypeReference
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.lang.reflect.ParameterizedType


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

    fun id(id: String) = apply { this.id = id }
    fun dtoMapper(dtoMapper: DtoMapper<T, *>) = apply { this.dtoMapper = dtoMapper }
    fun jpaSpecificationExecutor(jpaSpecificationExecutor: JpaSpecificationExecutor<T>) = apply { this.specificationExecutor = jpaSpecificationExecutor }
    fun entityJoinHandlers(vararg entityJoinHandlers: EntityJoinHandler<T>) = apply { this.entityJoinHandlers.addAll(entityJoinHandlers) }

    fun build(): SearchDescriptor<T> {
        require(::specificationExecutor.isInitialized) {
            "JPA SpecificationExecutor is required. JpaSpecificationExecutorFactory is not initialized with an EntityManager. Use 'jpaSpecificationExecutor' method."
        }

        return DefaultSearchDescriptor(id, entityClass, dtoMapper, specificationExecutor, entityJoinHandlers)
    }

    private data class DefaultSearchDescriptor<T> internal constructor(
            override val id: String,
            override val entityClass: Class<T>,
            override val dtoMapper: DtoMapper<T, *>,
            override val jpaSpecificationExecutor: JpaSpecificationExecutor<T>,
            override val entityJoinHandlers: List<EntityJoinHandler<T>>
    ) : SearchDescriptor<T>
}
