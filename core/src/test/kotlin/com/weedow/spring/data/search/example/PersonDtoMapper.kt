package com.weedow.spring.data.search.example

import com.weedow.spring.data.search.common.dto.PersonDto
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.dto.DtoMapper

class PersonDtoMapper : DtoMapper<Person, PersonDto> {

    override fun map(source: Person): PersonDto {
        return PersonDto.Builder()
            .firstName(source.firstName)
            .lastName(source.lastName)
            .build()
    }

}