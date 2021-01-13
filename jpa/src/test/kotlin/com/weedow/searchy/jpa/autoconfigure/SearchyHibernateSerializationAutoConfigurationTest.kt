package com.weedow.searchy.jpa.autoconfigure

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.FilteredClassLoader
import org.springframework.boot.test.context.runner.ApplicationContextRunner

internal class SearchyHibernateSerializationAutoConfigurationTest {

    @Test
    fun hibernateModule() {
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SearchyHibernateSerializationAutoConfiguration::class.java))
            .run { context ->
                assertThat(context).hasBean("hibernateModule")
                assertThat(context).hasSingleBean(Hibernate5Module::class.java)
            }
    }

    @Test
    fun hibernateModule_is_not_loaded_when_Hibernate5Module_not_present() {
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SearchyHibernateSerializationAutoConfiguration::class.java))
            .withClassLoader(FilteredClassLoader(Hibernate5Module::class.java))
            .run { context ->
                assertThat(context).doesNotHaveBean("hibernateModule")
                assertThat(context).doesNotHaveBean(Hibernate5Module::class.java)
            }
    }
}