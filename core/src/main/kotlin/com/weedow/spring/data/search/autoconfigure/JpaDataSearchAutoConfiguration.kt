package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.jpa.context.JpaDataSearchContext
import com.weedow.spring.data.search.querydsl.jpa.specification.JpaQueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactoryCachingDecorator
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

/**
 * Auto-Configuration for JPA implementation of Spring Data Search.
 */
@Configuration
class JpaDataSearchAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(EntityManager::class)
    fun jpaQueryDslSpecificationExecutorFactory(
        entityManager: EntityManager,
        dataSearchContext: DataSearchContext
    ): QueryDslSpecificationExecutorFactory {
        return QueryDslSpecificationExecutorFactoryCachingDecorator(JpaQueryDslSpecificationExecutorFactory(entityManager, dataSearchContext))
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DataSearchContext::class)
    fun jpaDataSearchContext(): DataSearchContext {
        return JpaDataSearchContext()
    }

}