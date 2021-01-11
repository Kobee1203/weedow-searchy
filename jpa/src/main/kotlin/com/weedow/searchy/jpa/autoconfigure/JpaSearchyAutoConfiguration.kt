package com.weedow.searchy.jpa.autoconfigure

import com.weedow.searchy.autoconfigure.SearchyAutoConfiguration
import com.weedow.searchy.config.SearchyConfigurationSupport
import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.dto.DtoMapper
import com.weedow.searchy.jpa.context.JpaSearchyContext
import com.weedow.searchy.jpa.dto.JpaDefaultDtoMapper
import com.weedow.searchy.jpa.query.specification.JpaSpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import com.weedow.searchy.query.specification.SpecificationExecutorFactoryCachingDecorator
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

/**
 * Auto-Configuration for JPA implementation of Searchy.
 */
@Configuration
@ConditionalOnClass(SearchyConfigurer::class)
@ConditionalOnMissingBean(SearchyConfigurationSupport::class)
@AutoConfigureBefore(SearchyAutoConfiguration::class)
class JpaSearchyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun <T> defaultDtoMapper(): DtoMapper<T, T> {
        return JpaDefaultDtoMapper()
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(EntityManager::class)
    fun jpaSpecificationExecutorFactory(
        entityManager: EntityManager,
        searchyContext: SearchyContext
    ): SpecificationExecutorFactory {
        return SpecificationExecutorFactoryCachingDecorator(JpaSpecificationExecutorFactory(entityManager, searchyContext))
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(SearchyContext::class)
    fun jpaSearchyContext(): SearchyContext {
        return JpaSearchyContext()
    }

}