package com.weedow.spring.data.search.example.dto

import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.example.model.Person

class PersonDtoMapper : DtoMapper<Person, PersonDto> {

    override fun map(source: Person): PersonDto {
        return PersonDto.Builder()
                .firstName(source.firstName)
                .lastName(source.lastName)
                .build()
    }

}