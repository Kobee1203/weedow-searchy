package com.weedow.searchy.sample.config

import com.weedow.searchy.common.model.Address
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.descriptor.SearchyDescriptorBuilder
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry
import com.weedow.searchy.join.handler.FetchingAllEntityJoinHandler
import com.weedow.searchy.jpa.join.handler.JpaFetchingEagerEntityJoinHandler
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.sample.dto.PersonDtoMapper
import com.weedow.searchy.sample.repository.PersonRepository
import com.weedow.searchy.validation.validator.EmailValidator
import com.weedow.searchy.validation.validator.NotEmptyValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SampleAppConfiguration : SearchyConfigurer {

    override fun addSearchyDescriptors(registry: SearchyDescriptorRegistry) {
        registry.addSearchyDescriptor(personSearchyDescriptor())
        registry.addSearchyDescriptor(addressSearchyDescriptor())
    }

    fun personSearchyDescriptor(): SearchyDescriptor<Person> = SearchyDescriptorBuilder.builder<Person>().build()

    @Bean
    fun person2SearchyDescriptor(personRepository: PersonRepository, searchyContext: SearchyContext): SearchyDescriptor<Person> =
        SearchyDescriptorBuilder.builder<Person>()
            .id("person2")
            .validators(NotEmptyValidator(), EmailValidator("email"))
            .dtoMapper(PersonDtoMapper())
            .specificationExecutor(personRepository)
            .entityJoinHandlers(JpaFetchingEagerEntityJoinHandler(searchyContext))
            .build()

    @Bean
    fun person3SearchyDescriptor(): SearchyDescriptor<Person> = SearchyDescriptorBuilder.builder<Person>()
        .id("person3")
        .entityJoinHandlers(FetchingAllEntityJoinHandler())
        .build()

    fun addressSearchyDescriptor(): SearchyDescriptor<Address> = SearchyDescriptorBuilder.builder<Address>().build()

    @Bean
    fun address3SearchyDescriptor(specificationExecutorFactory: SpecificationExecutorFactory): SearchyDescriptor<Address> =
        SearchyDescriptorBuilder.builder<Address>()
            .id("address2")
            .entityJoinHandlers(FetchingAllEntityJoinHandler())
            .specificationExecutor(specificationExecutorFactory.getSpecificationExecutor(Address::class.java))
            .build()

}