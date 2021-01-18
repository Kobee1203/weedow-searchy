package com.weedow.searchy.sample.mongodb.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.weedow.searchy.sample.mongodb.converter.DocumentToOffsetDateTimeConverter
import com.weedow.searchy.sample.mongodb.converter.DocumentToZonedDateTimeConverter
import com.weedow.searchy.sample.mongodb.converter.OffsetDateTimeToDocumentConverter
import com.weedow.searchy.sample.mongodb.converter.ZonedDateTimeToDocumentConverter
import com.weedow.searchy.sample.mongodb.model.Task
import com.weedow.searchy.sample.mongodb.serializer.TaskKeyDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.io.Resource
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean
import java.util.*


@Configuration
class SampleAppMongoDbConfiguration {

    @Value("classpath:/data.json")
    private lateinit var data: Resource

    @Bean
    fun repositoryPopulator(objectMapper: ObjectMapper): Jackson2RepositoryPopulatorFactoryBean {
        val factory = Jackson2RepositoryPopulatorFactoryBean()
        factory.setMapper(objectMapper)
        factory.setResources(arrayOf(data))
        return factory
    }

    @Bean
    fun customConversions(): MongoCustomConversions {
        val converters: MutableList<Converter<*, *>> = ArrayList<Converter<*, *>>()
        converters.add(DocumentToZonedDateTimeConverter)
        converters.add(ZonedDateTimeToDocumentConverter)
        converters.add(DocumentToOffsetDateTimeConverter)
        converters.add(OffsetDateTimeToDocumentConverter)
        return MongoCustomConversions(converters)
    }

    @Bean
    fun taskDeserializer(): com.fasterxml.jackson.databind.Module {
        val module = SimpleModule()
        module.addKeyDeserializer(Task::class.java, TaskKeyDeserializer())
        return module
    }

}