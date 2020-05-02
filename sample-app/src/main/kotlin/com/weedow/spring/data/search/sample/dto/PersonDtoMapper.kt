package com.weedow.spring.data.search.sample.dto

import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.sample.model.Person

class PersonDtoMapper : DtoMapper<Person, PersonDto> {

    override fun map(source: Person): PersonDto {
        return PersonDto.Builder()
                .firstName(source.firstName)
                .lastName(source.lastName)
                .email(source.email)
                .nickNames(source.nickNames)
                .phoneNumbers(source.phoneNumbers)
                .build()
    }

}