package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.config.JpaSpecificationExecutorFactory
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.querydsl.jpa.context.JpaDataSearchContext
import com.weedow.spring.data.search.querydsl.jpa.specification.JpaQueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactoryCachingDecorator
import com.weedow.spring.data.search.specification.JpaSpecificationService
import com.weedow.spring.data.search.specification.JpaSpecificationServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy
import javax.persistence.EntityManager

/**
 * Auto-Configuration for Spring Data Search.
 */
@Configuration
class JpaDataSearchAutoConfiguration {

    /**
     * Inject automatically the current [EntityManager] in order to initialize [JpaSpecificationExecutorFactory].
     */
    @Autowired(required = false)
    fun setEntityManager(entityManager: EntityManager) {
        JpaSpecificationExecutorFactory.init(entityManager)
    }

    /**
     * Reset [JpaSpecificationExecutorFactory].
     */
    @PreDestroy
    fun resetJpaSpecificationExecutorFactory() {
        JpaSpecificationExecutorFactory.reset()
    }

    /**
     * Bean to expose the [JpaSpecificationExecutorFactory].
     *
     * It is useful when an application creates an [SearchDescriptor] Bean without a specific [JpaSpecificationExecutor][org.springframework.data.jpa.repository.JpaSpecificationExecutor].
     * In this case, [@DependsOn][org.springframework.context.annotation.DependsOn] must be used to prevent an exception if the [SearchDescriptor] Bean is initialized before [JpaSpecificationExecutorFactory].
     *
     * ```java
     * @Configuration
     * public class SearchDescriptorConfiguration {
     *   @Bean
     *   @DependsOn("jpaSpecificationExecutorFactory")
     *   SearchDescriptor<Person> personSearchDescriptor() {
     *     return new SearchDescriptorBuilder<Person>(Person.class).build();
     *   }
     * }
     * ```
     */
    @Bean
    fun jpaSpecificationExecutorFactory(): JpaSpecificationExecutorFactory {
        return JpaSpecificationExecutorFactory
    }

    @Bean
    @ConditionalOnMissingBean
    fun jpaSpecificationService(): JpaSpecificationService {
        return JpaSpecificationServiceImpl()
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(EntityManager::class)
    fun jpaQueryDslSpecificationExecutorFactory(entityManager: EntityManager, dataSearchContext: DataSearchContext): QueryDslSpecificationExecutorFactory {
        return QueryDslSpecificationExecutorFactoryCachingDecorator(JpaQueryDslSpecificationExecutorFactory(entityManager, dataSearchContext))
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(DataSearchContext::class)
    fun jpaDataSearchContext(): DataSearchContext {
        return JpaDataSearchContext()
    }

}