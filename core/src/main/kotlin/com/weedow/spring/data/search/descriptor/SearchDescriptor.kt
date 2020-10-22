package com.weedow.spring.data.search.descriptor

import com.weedow.spring.data.search.dto.DefaultDtoMapper
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import com.weedow.spring.data.search.validation.DataSearchValidator
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
 *
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
 * * If the [SearchDescriptor] Bean is declared without a specific [JpaSpecificationExecutor][org.springframework.data.jpa.repository.JpaSpecificationExecutor],
 *   an exception may be thrown if the [SearchDescriptor] Bean is initialized before [JpaSpecificationExecutorFactory][com.weedow.spring.data.search.config.JpaSpecificationExecutorFactory].
 *   In this case, [@DependsOn][org.springframework.context.annotation.DependsOn] must be used to prevent the exception:
 *```java
 * @Configuration
 * public class SearchDescriptorConfiguration {
 *   @Bean
 *   @DependsOn("jpaSpecificationExecutorFactory")
 *   SearchDescriptor<Person> personSearchDescriptor() {
 *     return new SearchDescriptorBuilder<Person>(Person.class).build();
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
     * Returns list of [DataSearchValidators][DataSearchValidator] to validate the request parameters.
     */
    val validators: List<DataSearchValidator>
        get() = listOf()

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
     * Return list of [EntityJoinHandlers][EntityJoinHandler] to handle the entity joins.
     */
    @JvmDefault
    val entityJoinHandlers: List<EntityJoinHandler<T>>
        get() = listOf()
}