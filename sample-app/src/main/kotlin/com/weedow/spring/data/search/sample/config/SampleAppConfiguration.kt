package com.weedow.spring.data.search.sample.config

import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry
import com.weedow.spring.data.search.join.FetchingEagerEntityJoinHandler
import com.weedow.spring.data.search.sample.dto.PersonDtoMapper
import com.weedow.spring.data.search.sample.model.Address
import com.weedow.spring.data.search.sample.model.Person
import com.weedow.spring.data.search.sample.repository.PersonRepository
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
    fun person2SearchDescriptor(personRepository: PersonRepository): SearchDescriptor<Person> = SearchDescriptorBuilder.builder<Person>()
            .id("person2")
            .dtoMapper(PersonDtoMapper())
            .jpaSpecificationExecutor(personRepository)
            .entityJoinHandlers(FetchingEagerEntityJoinHandler())
            .build()

    fun addressSearchDescriptor(): SearchDescriptor<Address> = SearchDescriptorBuilder.builder<Address>().build()

}