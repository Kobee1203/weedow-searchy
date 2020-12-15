package com.weedow.spring.data.search.autoconfigure

import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.querydsl.specification.QueryDslSpecificationExecutorFactory
import com.weedow.spring.data.search.specification.JpaSpecificationService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

internal class JpaDataSearchAutoConfigurationTest {

    @Test
    fun initialize_jpa_beans() {
        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    JpaDataSearchAutoConfiguration::class.java,
                    TestEntityManagerConfiguration::class.java
                )
            )
            .run { context ->
                Assertions.assertThat(context).hasBean("jpaSpecificationExecutorFactory")
                Assertions.assertThat(context).hasBean("jpaSpecificationService")
                Assertions.assertThat(context).hasBean("jpaQueryDslSpecificationExecutorFactory")
                Assertions.assertThat(context).hasBean("jpaDataSearchContext")
            }
    }

    @Test
    fun initialize_custom_jpa_beans() {
        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    JpaDataSearchAutoConfiguration::class.java,
                    TestEntityManagerConfiguration::class.java
                )
            )
            .withUserConfiguration(TestCustomJpaConfiguration::class.java)
            .run { context ->
                Assertions.assertThat(context).hasBean("jpaSpecificationExecutorFactory")
                Assertions.assertThat(context).doesNotHaveBean("jpaSpecificationService")
                Assertions.assertThat(context).doesNotHaveBean("jpaQueryDslSpecificationExecutorFactory")
                Assertions.assertThat(context).doesNotHaveBean("jpaDataSearchContext")
                Assertions.assertThat(context).hasBean("customJpaSpecificationService")
                Assertions.assertThat(context).hasBean("customJpaQueryDslSpecificationExecutorFactory")
                Assertions.assertThat(context).hasBean("customJpaDataSearchContext")
            }
    }

    @Configuration
    class TestEntityManagerConfiguration {

        @Bean
        fun testEntityManager(): EntityManager = mock()

    }

    @Configuration
    class TestCustomJpaConfiguration {

        @Bean
        fun customJpaSpecificationService(): JpaSpecificationService = mock()

        @Bean
        fun customJpaQueryDslSpecificationExecutorFactory(): QueryDslSpecificationExecutorFactory = mock()

        @Bean
        fun customJpaDataSearchContext(): DataSearchContext = mock()

    }

}