package com.weedow.searchy.mongodb.autoconfigure

import com.weedow.searchy.autoconfigure.SearchyAutoConfiguration
import com.weedow.searchy.config.SearchyConfigurationSupport
import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.mongodb.context.MongoSearchyContext
import com.weedow.searchy.mongodb.query.specification.MongoSpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutorFactoryCachingDecorator
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoOperations

/**
 * Auto-Configuration for MongoDB implementation of Searchy.
 */
@Configuration
@ConditionalOnClass(SearchyConfigurer::class)
@ConditionalOnMissingBean(SearchyConfigurationSupport::class)
@AutoConfigureBefore(SearchyAutoConfiguration::class)
class MongoSearchyAutoConfiguration {

    /*
    @Bean
    @ConditionalOnMissingBean
    fun <T> defaultDtoMapper(): DtoMapper<T, T> {
        return MongoDefaultDtoMapper()
    }
    */

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