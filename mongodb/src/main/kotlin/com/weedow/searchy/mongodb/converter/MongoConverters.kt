package com.weedow.searchy.mongodb.converter

/**
 * Wrapper class to register multiple [MongoConverter]s in [MongoCustomConversions][org.springframework.data.mongodb.core.convert.MongoCustomConversions] for the usage with Mongo.
 *
 * A new instance this class must be declared as `Bean` to be registered in [MongoCustomConversions][org.springframework.data.mongodb.core.convert.MongoCustomConversions].
 *
 * @param converters List of [MongoConverter]s to be registered
 */
class MongoConverters private constructor(
    converters: List<MongoConverter<*, *>>
) {
    val converters: List<MongoConverter<*, *>> = java.util.List.copyOf(converters)

    companion object {
        /**
         * Create a new instance of [MongoConverters] that contains the given [MongoConverter]s.
         */
        fun of(vararg converters: MongoConverter<*, *>): MongoConverters {
            return MongoConverters(converters.toList())
        }

        /**
         * Create a new instance of [MongoConverters] that contains the given list of [MongoConverter]s.
         */
        fun of(converters: List<MongoConverter<*, *>>): MongoConverters {
            return MongoConverters(converters)
        }
    }
}