package com.weedow.spring.data.search.sample.dto

data class PersonDto private constructor(
        val firstName: String?,
        val lastName: String?,
        val email: String?,
        val nickNames: Set<String>?,
        val phoneNumbers: Set<String>?,
        val addresses: Set<AddressDto>?,
        val vehicles: Set<VehicleDto>?
) {

    data class Builder(
            private var firstName: String? = null,
            private var lastName: String? = null,
            private var email: String? = null,
            private var nickNames: Set<String>? = null,
            private var phoneNumbers: Set<String>? = null,
            private var addresses: Set<AddressDto>? = null,
            private var vehicles: Set<VehicleDto>? = null
    ) {
        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun email(email: String?) = apply { this.email = email }
        fun nickNames(nickNames: Set<String>?) = apply { this.nickNames = nickNames }
        fun phoneNumbers(phoneNumbers: Set<String>?) = apply { this.phoneNumbers = phoneNumbers }
        fun addresses(addresses: Set<AddressDto>?) = apply { this.addresses = addresses }
        fun vehicles(vehicles: Set<VehicleDto>?) = apply { this.vehicles = vehicles }
        fun build() = PersonDto(firstName, lastName, email, nickNames, phoneNumbers, addresses, vehicles)
    }

}