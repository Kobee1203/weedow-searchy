package com.weedow.searchy.jpa.autoconfigure

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.specification.SpecificationExecutorFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

internal class JpaSearchyAutoConfigurationTest {

    @Test
    fun initialize_jpa_beans() {
        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    JpaSearchyAutoConfiguration::class.java,
                    TestEntityManagerConfiguration::class.java
                )
            )
            .run { context ->
                assertThat(context).hasBean("jpaSpecificationExecutorFactory")
                assertThat(context).hasBean("jpaSearchyContext")
            }
    }

    @Test
    fun initialize_custom_jpa_beans() {
        ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    JpaSearchyAutoConfiguration::class.java,
                    TestEntityManagerConfiguration::class.java
                )
            )
            .withUserConfiguration(TestCustomJpaConfiguration::class.java)
            .run { context ->
                assertThat(context).doesNotHaveBean("jpaSpecificationExecutorFactory")
                assertThat(context).doesNotHaveBean("jpaSearchyContext")
                assertThat(context).hasBean("customJpaSpecificationExecutorFactory")
                assertThat(context).hasBean("customJpaSearchyContext")
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
        fun customJpaSpecificationExecutorFactory(): SpecificationExecutorFactory = mock()

        @Bean
        fun customJpaSearchyContext(): SearchyContext = mock()

    }

}