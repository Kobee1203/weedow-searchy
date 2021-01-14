package com.weedow.searchy.sample.dto

import com.weedow.searchy.common.dto.AddressDto
import com.weedow.searchy.common.dto.PersonDto
import com.weedow.searchy.common.dto.VehicleDto
import com.weedow.searchy.common.model.Address
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.common.model.Vehicle
import com.weedow.searchy.dto.DtoMapper


class PersonDtoMapper : DtoMapper<Person, PersonDto> {

    override fun map(source: Person): PersonDto {
        return PersonDto.Builder()
                .firstName(source.firstName)
                .lastName(source.lastName)
                .email(source.email)
                .nickNames(source.nickNames)
                .phoneNumbers(source.phoneNumbers?.toSet())
                .addresses(mapAddresses(source.addressEntities))
                .vehicles(mapVehicles(source.vehicles))
                .build()
    }

    private fun mapAddresses(addressEntities: Set<Address>?): Set<AddressDto>? {
        return addressEntities?.mapTo(mutableSetOf()) {
            AddressDto.Builder()
                    .street(it.street)
                    .city(it.city)
                    .zipCode(it.zipCode)
                    .country(it.country)
                    .build()
        }
    }

    private fun mapVehicles(vehicles: Set<Vehicle>?): Set<VehicleDto>? {
        return vehicles?.mapTo(mutableSetOf()) {
            VehicleDto.Builder()
                    .vehicleType(it.vehicleType)
                    .brand(it.brand)
                    .model(it.model)
                    .build()
        }
    }

}