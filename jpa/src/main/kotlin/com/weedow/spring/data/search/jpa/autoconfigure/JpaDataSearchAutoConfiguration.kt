package com.weedow.spring.data.search.jpa.autoconfigure

import com.weedow.spring.data.search.autoconfigure.DataSearchAutoConfiguration
import com.weedow.spring.data.search.config.SearchConfigurationSupport
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.dto.DtoMapper
import com.weedow.spring.data.search.jpa.context.JpaDataSearchContext
import com.weedow.spring.data.search.jpa.dto.JpaDefaultDtoMapper
import com.weedow.spring.data.search.jpa.query.specification.JpaSpecificationExecutorFactory
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactoryCachingDecorator
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

/**
 * Auto-Configuration for JPA implementation of Spring Data Search.
 */
@Configuration
@ConditionalOnClass(SearchConfigurer::class)
@ConditionalOnMissingBean(SearchConfigurationSupport::class)
@AutoConfigureBefore(DataSearchAutoConfiguration::class)
class JpaDataSearchAutoConfiguration {

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
        dataSearchContext: DataSearchContext
    ): SpecificationExecutorFactory {
        return SpecificationExecutorFactoryCachingDecorator(JpaSpecificationExecutorFactory(entityManager, dataSearchContext))
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DataSearchContext::class)
    fun jpaDataSearchContext(): DataSearchContext {
        return JpaDataSearchContext()
    }

}