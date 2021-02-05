package com.weedow.searchy.mongodb.autoconfigure

import com.weedow.searchy.autoconfigure.SearchyAutoConfiguration
import com.weedow.searchy.config.SearchyConfigurationSupport
import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.mongodb.context.MongoSearchyContext
import com.weedow.searchy.mongodb.converter.*
import com.weedow.searchy.mongodb.domain.DbSequence
import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorServiceImpl
import com.weedow.searchy.mongodb.event.*
import com.weedow.searchy.mongodb.query.specification.MongoSpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutorFactoryCachingDecorator
import com.weedow.searchy.utils.klogger
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mapping.context.PersistentEntities
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import kotlin.streams.toList

/**
 * Auto-Configuration for MongoDB implementation of Searchy.
 */
@Configuration
@ConditionalOnClass(SearchyConfigurer::class, MongoOperations::class)
@ConditionalOnMissingBean(SearchyConfigurationSupport::class)
@AutoConfigureBefore(SearchyAutoConfiguration::class)
class MongoSearchyAutoConfiguration {

    companion object {
        private val log by klogger()
    }

    @Bean
    fun searchyMongoConverters(): MongoConverters {
        return MongoConverters.of(
            DocumentToZonedDateTimeConverter,
            ZonedDateTimeToDocumentConverter,
            DocumentToOffsetDateTimeConverter,
            OffsetDateTimeToDocumentConverter
        )
    }

    @Bean
    fun mongoSearchyConversions(singleMongoConverters: ObjectProvider<MongoConverter<*, *>>, multipleMongoConverters: ObjectProvider<MongoConverters>): MongoCustomConversions {
        val allConverters = listOf(
            singleMongoConverters.orderedStream().toList(),
            multipleMongoConverters.orderedStream().toList().flatMap { it.converters }
        ).flatten()
        return MongoCustomConversions(allConverters)
    }

    @Bean
    @ConditionalOnMissingBean
    fun dbSequenceGeneratorService(mongoOperations: MongoOperations, mongoMappingContext: MongoMappingContext): DbSequenceGeneratorService {
        // Registering the 'DbSequence' collection
        val persistentEntity = mongoMappingContext.getPersistentEntity(DbSequence::class.java)
        if (log.isDebugEnabled) {
            if (persistentEntity != null) {
                val collectionFields = persistentEntity.joinToString { it.fieldName + "[" + it.fieldType.simpleName + "]" }
                val collectionInfos = "${persistentEntity.collection} ($collectionFields)"
                log.debug("'DbSequence' collection is registered to Mongo Mapping Context: {}", collectionInfos)
            } else {
                log.warn("'DbSequence' collection is NOT registered to Mongo Mapping Context!")
            }
        }
        return DbSequenceGeneratorServiceImpl(mongoOperations)
    }

    @Bean
    fun longIdMongoPersistablePrePersistEntityHandler(dbSequenceGeneratorService: DbSequenceGeneratorService): PrePersistEntityHandler {
        return LongIdMongoPersistablePrePersistEntityHandler(dbSequenceGeneratorService)
    }

    @Bean
    fun longIdAnnotationPrePersistEntityHandler(dbSequenceGeneratorService: DbSequenceGeneratorService): PrePersistEntityHandler {
        return LongIdAnnotationPrePersistEntityHandler(dbSequenceGeneratorService)
    }

    @Bean
    fun longIdPrePersistEntityHandler(dbSequenceGeneratorService: DbSequenceGeneratorService): PrePersistEntityHandler {
        return LongIdPrePersistEntityHandler(dbSequenceGeneratorService)
    }

    @Bean
    @ConditionalOnMissingBean
    fun isNewEntityMongoListener(
        mongoMappingContext: MongoMappingContext,
        prePersistEntityHandlers: ObjectProvider<PrePersistEntityHandler>,
        preUpdateEntityHandlers: ObjectProvider<PreUpdateEntityHandler>
    ): IsNewEntityMongoListener {
        return IsNewEntityMongoListener(
            PersistentEntities.of(mongoMappingContext),
            prePersistEntityHandlers.orderedStream().toList(),
            preUpdateEntityHandlers.orderedStream().toList()
        )
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(MongoOperations::class)
    fun mongoSpecificationExecutorFactory(
        mongoOperations: MongoOperations,
        searchyContext: SearchyContext
    ): SpecificationExecutorFactory {
        return SpecificationExecutorFactoryCachingDecorator(MongoSpecificationExecutorFactory(mongoOperations, searchyContext))
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(SearchyContext::class)
    fun mongoSearchyContext(): SearchyContext {
        return MongoSearchyContext()
    }

}