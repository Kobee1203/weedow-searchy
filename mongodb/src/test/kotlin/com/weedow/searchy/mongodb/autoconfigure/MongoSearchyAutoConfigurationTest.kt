package com.weedow.searchy.mongodb.autoconfigure

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.mongodb.converter.*
import com.weedow.searchy.mongodb.domain.DbSequence
import com.weedow.searchy.mongodb.event.*
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import org.apache.commons.lang3.reflect.FieldUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory
import org.springframework.core.convert.converter.ConverterRegistry
import org.springframework.core.convert.converter.GenericConverter
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.util.ClassTypeInformation

internal class MongoSearchyAutoConfigurationTest {

    @Test
    fun initialize_mongodb_beans() {
        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestMongoConfiguration::class.java,
                    MongoSearchyAutoConfiguration::class.java
                )
            )
            .run { context ->
                assertThat(context).hasBean("searchyMongoConverters")
                assertThat(context).hasBean("mongoSearchyConversions")
                assertThat(context).hasBean("dbSequenceGeneratorService")
                assertThat(context).hasBean("longIdMongoPersistablePrePersistEntityHandler")
                assertThat(context).hasBean("longIdAnnotationPrePersistEntityHandler")
                assertThat(context).hasBean("longIdPrePersistEntityHandler")
                assertThat(context).hasBean("isNewEntityMongoListener")
                assertThat(context).hasBean("mongoSpecificationExecutorFactory")
                assertThat(context).hasBean("mongoSearchyContext")

                val mongoConverters = context.getBean("searchyMongoConverters", MongoConverters::class.java)
                assertThat(mongoConverters.converters).containsExactly(
                    DocumentToZonedDateTimeConverter,
                    ZonedDateTimeToDocumentConverter,
                    DocumentToOffsetDateTimeConverter,
                    OffsetDateTimeToDocumentConverter
                )

                val mongoCustomConversions = context.getBean("mongoSearchyConversions", MongoCustomConversions::class.java)
                val registration = TestConverterRegistration()
                mongoCustomConversions.registerConvertersIn(registration)
                assertThat(registration.getConverters())
                    .hasSize(66) // 63 Converters + 3 GenericConverters
                    .contains(
                        DocumentToZonedDateTimeConverter,
                        ZonedDateTimeToDocumentConverter,
                        DocumentToOffsetDateTimeConverter,
                        OffsetDateTimeToDocumentConverter
                    )

                val isNewEntityMongoListener = context.getBean("isNewEntityMongoListener", IsNewEntityMongoListener::class.java)
                val prePersistEntityHandlers =
                    FieldUtils.readField(isNewEntityMongoListener, "prePersistEntityHandlers", true) as List<PrePersistEntityHandler>
                assertThat(prePersistEntityHandlers).extracting("class").containsExactly(
                    LongIdMongoPersistablePrePersistEntityHandler::class.java,
                    LongIdAnnotationPrePersistEntityHandler::class.java,
                    LongIdPrePersistEntityHandler::class.java
                )
                val preUpdateEntityHandlers =
                    FieldUtils.readField(isNewEntityMongoListener, "preUpdateEntityHandlers", true) as List<PreUpdateEntityHandler>
                assertThat(preUpdateEntityHandlers).isEmpty()
            }
    }

    @Test
    fun initialize_custom_mongodb_beans() {
        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestMongoConfiguration::class.java,
                    MongoSearchyAutoConfiguration::class.java
                )
            )
            .withUserConfiguration(TestCustomMongoConfiguration::class.java)
            .run { context ->
                assertThat(context).hasBean("searchyMongoConverters")
                assertThat(context).hasBean("mongoSearchyConversions")
                assertThat(context).hasBean("dbSequenceGeneratorService")
                assertThat(context).hasBean("longIdMongoPersistablePrePersistEntityHandler")
                assertThat(context).hasBean("longIdAnnotationPrePersistEntityHandler")
                assertThat(context).hasBean("longIdPrePersistEntityHandler")
                assertThat(context).hasBean("isNewEntityMongoListener")
                assertThat(context).doesNotHaveBean("mongoSpecificationExecutorFactory")
                assertThat(context).doesNotHaveBean("mongoSearchyContext")
                assertThat(context).hasBean("customMongoSpecificationExecutorFactory")
                assertThat(context).hasBean("customMongoSearchyContext")

                val mongoConverters = context.getBean("searchyMongoConverters", MongoConverters::class.java)
                assertThat(mongoConverters.converters).containsExactly(
                    DocumentToZonedDateTimeConverter,
                    ZonedDateTimeToDocumentConverter,
                    DocumentToOffsetDateTimeConverter,
                    OffsetDateTimeToDocumentConverter
                )

                val mongoCustomConversions = context.getBean("mongoSearchyConversions", MongoCustomConversions::class.java)
                val registration = TestConverterRegistration()
                mongoCustomConversions.registerConvertersIn(registration)
                assertThat(registration.getConverters())
                    .hasSize(69) // 63 Converters + 3 GenericConverters + 3 Custom Converters
                    .contains(
                        DocumentToZonedDateTimeConverter,
                        ZonedDateTimeToDocumentConverter,
                        DocumentToOffsetDateTimeConverter,
                        OffsetDateTimeToDocumentConverter,
                        CustomMongoConverter1,
                        CustomMongoConverter2,
                        CustomMongoConverter3
                    )

                val isNewEntityMongoListener = context.getBean("isNewEntityMongoListener", IsNewEntityMongoListener::class.java)
                val prePersistEntityHandlers =
                    FieldUtils.readField(isNewEntityMongoListener, "prePersistEntityHandlers", true) as List<PrePersistEntityHandler>
                assertThat(prePersistEntityHandlers).extracting("class").containsExactly(
                    LongIdMongoPersistablePrePersistEntityHandler::class.java,
                    LongIdAnnotationPrePersistEntityHandler::class.java,
                    LongIdPrePersistEntityHandler::class.java,
                    CustomPrePersistEntityHandler::class.java
                )
                val preUpdateEntityHandlers =
                    FieldUtils.readField(isNewEntityMongoListener, "preUpdateEntityHandlers", true) as List<PreUpdateEntityHandler>
                assertThat(preUpdateEntityHandlers).extracting("class").containsExactly(CustomPreUpdateEntityHandler::class.java)
            }
    }

    internal class TestConverterRegistration : ConverterRegistry {

        private val converters = mutableListOf<Converter<*, *>>()
        private val genericConverters = mutableListOf<GenericConverter>()

        override fun addConverter(converter: Converter<*, *>) {
            converters.add(converter)
        }

        override fun <S : Any?, T : Any?> addConverter(sourceType: Class<S>, targetType: Class<T>, converter: Converter<in S, out T>) {
            converters.add(converter)
        }

        override fun addConverter(converter: GenericConverter) {
            genericConverters.add(converter)
        }

        override fun addConverterFactory(factory: ConverterFactory<*, *>) {
            // DO NOTHING
        }

        override fun removeConvertible(sourceType: Class<*>, targetType: Class<*>) {
            // DO NOTHING
        }

        fun getConverters(): List<*> = listOf(converters, genericConverters).flatten()
    }

    @Configuration
    class TestMongoConfiguration {

        @Bean
        fun mongoOperations(): MongoOperations = mock()

        @Bean
        fun mongoMappingContext(): MongoMappingContext = mock {
            on { this.getPersistentEntity(DbSequence::class.java) }
                .thenReturn(BasicMongoPersistentEntity(ClassTypeInformation.from(DbSequence::class.java)))
        }

    }

    @Configuration
    class TestCustomMongoConfiguration {

        @Bean
        fun customMongoSpecificationExecutorFactory(): SpecificationExecutorFactory = mock()

        @Bean
        fun customMongoSearchyContext(): SearchyContext = mock()

        @Bean
        fun customMongoConverter(): MongoConverter<*, *> = CustomMongoConverter1

        @Bean
        fun customMongoConverters(): MongoConverters {
            return MongoConverters.of(
                CustomMongoConverter2,
                CustomMongoConverter3,
            )
        }

        @Bean
        fun customPrePersistEntityHandler(): PrePersistEntityHandler = CustomPrePersistEntityHandler()

        @Bean
        fun customPreUpdateEntityHandler(): PreUpdateEntityHandler = CustomPreUpdateEntityHandler()

    }

    internal object CustomMongoConverter1 : MongoConverter<Any, Any> {
        override fun convert(source: Any): Any = source
    }

    internal object CustomMongoConverter2 : MongoConverter<Any, Any> {
        override fun convert(source: Any): Any = source
    }

    internal object CustomMongoConverter3 : MongoConverter<Any, Any> {
        override fun convert(source: Any): Any = source
    }

    internal class CustomPrePersistEntityHandler : PrePersistEntityHandler {
        override fun supports(entity: Any): Boolean = true

        override fun handle(entity: Any) {}
    }

    internal class CustomPreUpdateEntityHandler : PreUpdateEntityHandler {
        override fun supports(entity: Any): Boolean = true

        override fun handle(entity: Any) {}
    }
}