package com.weedow.searchy.mongodb.converter

import org.springframework.core.convert.converter.Converter

interface MongoConverter<S, T> : Converter<S, T>