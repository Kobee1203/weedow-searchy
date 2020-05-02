package com.weedow.spring.data.search.example.dto

data class PersonDto private constructor(
        val firstName: String?,
        val lastName: String?
) {

    data class Builder(
            private var firstName: String? = null,
            private var lastName: String? = null
    ) {
        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun build() = PersonDto(firstName, lastName)
    }

}