package com.weedow.searchy.descriptor

import com.weedow.searchy.dto.DtoMapper
import com.weedow.searchy.join.handler.EntityJoinHandler
import com.weedow.searchy.query.specification.SpecificationExecutor
import com.weedow.searchy.validation.SearchyValidator

/**
 * Search Descriptor allows exposing automatically a search endpoint for an Entity.
 *
 * The new endpoint is mapped to `/search/{searchyDescriptorId}` where `searchyDescriptorId` is the [id] specified in the `SearchyDescriptor`.
 *
 * The easiest way to create a Search Descriptor is to use the [SearchyDescriptorBuilder] which provides every options available to configure
 * a `SearchyDescriptor`.
 *
 * To expose the new Entity search endpoint, `SearchyDescriptor` must be registered to the Searchy Configuration:
 * * Implement the [SearchyConfigurer][com.weedow.searchy.config.SearchyConfigurer] interface and override the
 * [addSearchyDescriptors][com.weedow.searchy.config.SearchyConfigurer.addSearchyDescriptors] method:
 * ```java
 * @Configuration
 * public class SearchyDescriptorConfiguration implements SearchyConfigurer {
 *
 *   @Override
 *   public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
 *     SearchyDescriptor searchyDescriptor = new SearchyDescriptorBuilder<Person>(Person.class).build();
 *     registry.addSearchyDescriptor(searchyDescriptor);
 *   }
 * }
 * ```
 *
 * * Another solution is to add a new [@Bean][org.springframework.context.annotation.Bean]. This solution is useful when you want to create
 * a `SearchyDescriptor` which depends on other Beans:
 * ```java
 * @Configuration
 * public class SearchyDescriptorConfiguration {
 *   @Bean
 *   SearchyDescriptor<Person> personSearchyDescriptor(PersonRepository personRepository) {
 *     return new SearchyDescriptorBuilder<Person>(Person.class)
 *                      .specificationExecutor(personRepository)
 *                      .build();
 *   }
 * }
 * ```
 *
 * @param T Entity type
 *
 * @see com.weedow.searchy.descriptor.SearchyDescriptorBuilder
 * @see com.weedow.searchy.config.SearchyConfigurer
 */
interface SearchyDescriptor<T> {

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
     * Returns list of [SearchyValidators][SearchyValidator] to validate the request parameters.
     */
    val validators: List<SearchyValidator>
        get() = listOf()

    /**
     * Return the mapper to convert the entity to a specific DTO.
     *
     * By default, the method returns a mapper that just returns the given entity.
     *
     * @param <R> represents the DTO type
     * @return DTO Object
     */
    val dtoMapper: DtoMapper<T, *>?

    /**
     * Returns the [SpecificationExecutor] to search the entities.
     *
     * If it's not defined, it will be identified automatically by the instance of
     * [SpecificationExecutorFactory][com.weedow.searchy.query.specification.SpecificationExecutorFactory].
     *
     * @return SpecificationExecutor
     * @see SpecificationExecutor
     * @see com.weedow.searchy.query.specification.SpecificationExecutorFactory
     */
    val specificationExecutor: SpecificationExecutor<T>?

    /**
     * Return list of [EntityJoinHandlers][EntityJoinHandler] to handle the entity joins.
     */
    @JvmDefault
    val entityJoinHandlers: List<EntityJoinHandler>
        get() = listOf()
}