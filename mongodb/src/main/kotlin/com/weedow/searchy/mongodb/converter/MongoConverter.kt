package com.weedow.searchy.mongodb.converter

import org.springframework.core.convert.converter.Converter

/**
 * A converter converts a source object of type S to a target of type T.
 *
 * This interface is only used to create custom converters for MongoDB.
 *
 * The implementations must be declared as `Bean` to be registered in [MongoCustomConversions][org.springframework.data.mongodb.core.convert.MongoCustomConversions].
 *
 * @param S The source type
 * @param T The target type
 */
interface MongoConverter<S, T> : Converter<S, T>