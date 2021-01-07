package com.weedow.spring.data.search.sample.config

import com.weedow.spring.data.search.common.model.Address
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import com.weedow.spring.data.search.join.handler.FetchingAllEntityJoinHandler
import com.weedow.spring.data.search.jpa.join.handler.JpaFetchingEagerEntityJoinHandler
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
import com.weedow.spring.data.search.sample.dto.PersonDtoMapper
import com.weedow.spring.data.search.sample.repository.PersonRepository
import com.weedow.spring.data.search.validation.validator.EmailValidator
import com.weedow.spring.data.search.validation.validator.NotEmptyValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SampleAppConfiguration : SearchConfigurer {

    override fun addSearchDescriptors(registry: SearchDescriptorRegistry) {
        registry.addSearchDescriptor(personSearchDescriptor())
        registry.addSearchDescriptor(addressSearchDescriptor())
    }

    fun personSearchDescriptor(): SearchDescriptor<Person> = SearchDescriptorBuilder.builder<Person>().build()

    @Bean
    fun person2SearchDescriptor(personRepository: PersonRepository, dataSearchContext: DataSearchContext): SearchDescriptor<Person> =
        SearchDescriptorBuilder.builder<Person>()
            .id("person2")
            .validators(NotEmptyValidator(), EmailValidator("email"))
            .dtoMapper(PersonDtoMapper())
            .specificationExecutor(personRepository)
            .entityJoinHandlers(JpaFetchingEagerEntityJoinHandler(dataSearchContext))
            .build()

    @Bean
    fun person3SearchDescriptor(): SearchDescriptor<Person> = SearchDescriptorBuilder.builder<Person>()
        .id("person3")
        .entityJoinHandlers(FetchingAllEntityJoinHandler())
        .build()

    fun addressSearchDescriptor(): SearchDescriptor<Address> = SearchDescriptorBuilder.builder<Address>().build()

    @Bean
    fun address3SearchDescriptor(specificationExecutorFactory: SpecificationExecutorFactory): SearchDescriptor<Address> =
        SearchDescriptorBuilder.builder<Address>()
            .id("address2")
            .entityJoinHandlers(FetchingAllEntityJoinHandler())
            .specificationExecutor(specificationExecutorFactory.getSpecificationExecutor(Address::class.java))
            .build()

}