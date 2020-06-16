package com.weedow.spring.data.search.descriptor

import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * Search Descriptor allows exposing automatically a search endpoint for a JPA Entity.
 *
 * The new endpoint is mapped to `/search/{searchDescriptorId}` where `searchDescriptorId` is the [id] specified in the `SearchDescriptor`.
 *
 * The easiest way to create a Search Descriptor is to use the [SearchDescriptorBuilder] which provides every options available to configure a `SearchDescriptor`.
 *
 * To expose the new Entity search endpoint, `SearchDescriptor` must be registered to the Spring Data Search Configuration:
 * * Implement the [SearchConfigurer][com.weedow.spring.data.search.config.SearchConfigurer] interface and override the [addSearchDescriptors][com.weedow.spring.data.search.config.SearchConfigurer.addSearchDescriptors] method:
 * ```java
 * @Configuration
 * public class SearchDescriptorConfiguration implements SearchConfigurer {
 *
 *   @Override
 *   public void addSearchDescriptors(SearchDescriptorRegistry registry) {
 *     SearchDescriptor searchDescriptor = new SearchDescriptorBuilder<Person>(Person.class).build();
 *     registry.addSearchDescriptor(searchDescriptor);
 *   }
 * }
 * ```
 * * Another solution is to add a new [@Bean][org.springframework.context.annotation.Bean]. This solution is useful when you want to create a `SearchDescriptor` which depends on other Beans:
 * ```java
 * @Configuration
 * public class SearchDescriptorConfiguration {
 *   @Bean
 *   SearchDescriptor<Person> personSearchDescriptor(PersonRepository personRepository) {
 *     return new SearchDescriptorBuilder<Person>(Person.class)
 *                      .jpaSpecificationExecutor(personRepository)
 *                      .build();
 *   }
 * }
 * ```
 *
 * @param T Entity type
 *
 * @see com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder
 * @see com.weedow.spring.data.search.config.SearchConfigurer
 */
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
    @JvmDefault
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
     * Return list of [EntityJoinHandler]s to handle the entity joins.
     */
    @JvmDefault
    val entityJoinHandlers: List<EntityJoinHandler<T>>
        get() = listOf()
}