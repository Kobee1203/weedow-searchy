package com.weedow.searchy.mongodb.converter

class MongoConverters private constructor(
    converters: List<MongoConverter<*, *>>
) {
    val converters: List<MongoConverter<*, *>> = java.util.List.copyOf(converters)

    companion object {
        fun of(vararg converters: MongoConverter<*, *>): MongoConverters {
            return MongoConverters(converters.toList())
        }

        fun of(converters: List<MongoConverter<*, *>>): MongoConverters {
            return MongoConverters(converters)
        }
    }
}