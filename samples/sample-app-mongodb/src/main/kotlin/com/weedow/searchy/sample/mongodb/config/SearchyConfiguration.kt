package com.weedow.searchy.sample.mongodb.config

import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.descriptor.SearchyDescriptorBuilder
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry
import com.weedow.searchy.join.handler.FetchingAllEntityJoinHandler
import com.weedow.searchy.sample.mongodb.model.Person
import com.weedow.searchy.sample.mongodb.repository.PersonRepository
import com.weedow.searchy.validation.validator.EmailValidator
import com.weedow.searchy.validation.validator.NotEmptyValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchyConfiguration : SearchyConfigurer {

    override fun addSearchyDescriptors(registry: SearchyDescriptorRegistry) {
        registry.addSearchyDescriptor(personSearchyDescriptor())
    }

    fun personSearchyDescriptor(): SearchyDescriptor<Person> = SearchyDescriptorBuilder.builder<Person>().build()

    @Bean
    fun person2SearchyDescriptor(personRepository: PersonRepository, searchyContext: SearchyContext): SearchyDescriptor<Person> =
        SearchyDescriptorBuilder.builder<Person>()
            .id("person2")
            .validators(NotEmptyValidator(), EmailValidator("email"))
            //.dtoMapper(PersonDtoMapper())
            //.specificationExecutor(personRepository)
            //.entityJoinHandlers(JpaFetchingEagerEntityJoinHandler(searchyContext))
            .build()

    @Bean
    fun person3SearchyDescriptor(): SearchyDescriptor<Person> = SearchyDescriptorBuilder.builder<Person>()
        .id("person3")
        .entityJoinHandlers(FetchingAllEntityJoinHandler())
        .build()

}