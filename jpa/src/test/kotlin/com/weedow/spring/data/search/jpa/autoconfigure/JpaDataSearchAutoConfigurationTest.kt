package com.weedow.spring.data.search.jpa.autoconfigure

import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.query.specification.SpecificationExecutorFactory
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
                Assertions.assertThat(context).doesNotHaveBean("jpaSpecificationExecutorFactory")
                Assertions.assertThat(context).doesNotHaveBean("jpaDataSearchContext")
                Assertions.assertThat(context).hasBean("customJpaSpecificationExecutorFactory")
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
        fun customJpaSpecificationExecutorFactory(): SpecificationExecutorFactory = mock()

        @Bean
        fun customJpaDataSearchContext(): DataSearchContext = mock()

    }

}