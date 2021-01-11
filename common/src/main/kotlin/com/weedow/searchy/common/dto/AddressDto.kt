package com.weedow.searchy.common.dto

import com.neovisionaries.i18n.CountryCode

data class AddressDto internal constructor(
    val street: String?,
    val city: String?,
    val zipCode: String?,
    val country: String?
) {

    data class Builder(
        private var street: String? = null,
        private var city: String? = null,
        private var zipCode: String? = null,
        private var country: String? = null
    ) {
        fun street(street: String) = apply { this.street = street }
        fun city(city: String) = apply { this.city = city }
        fun zipCode(zipCode: String) = apply { this.zipCode = zipCode }
        fun country(country: CountryCode) = apply { this.country = country.getName() }
        fun build() = AddressDto(street, city, zipCode, country)
    }

}