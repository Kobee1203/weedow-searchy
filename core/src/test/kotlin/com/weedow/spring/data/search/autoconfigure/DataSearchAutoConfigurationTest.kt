package com.weedow.spring.data.search.autoconfigure

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.controller.reactive.DataSearchReactiveController
import com.weedow.spring.data.search.controller.servlet.DataSearchController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.test.context.FilteredClassLoader
import org.springframework.boot.test.context.runner.WebApplicationContextRunner


internal class DataSearchAutoConfigurationTest {

    private val contextRunner = WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DataSearchAutoConfiguration::class.java, WebMvcAutoConfiguration::class.java))

    @Test
    fun dataSearchAutoConfiguration_is_loaded() {
        contextRunner
                .run { context ->
                    assertThat(context).hasSingleBean(DataSearchController::class.java)
                    assertThat(context).doesNotHaveBean(DataSearchReactiveController::class.java)
                    assertThat(context).hasBean("fieldPathResolver")
                    assertThat(context).hasBean("fieldInfoResolver")
                    assertThat(context).hasBean("expressionMapper")
                    assertThat(context).hasBean("expressionParserVisitorFactory")
                    assertThat(context).hasBean("expressionParser")
                    assertThat(context).hasBean("dataSearchService")
                    assertThat(context).hasBean("dataSearchValidationService")
                    assertThat(context).hasBean("dataSearchErrorsFactory")
                    assertThat(context).hasBean("entityJoinManager")
                    assertThat(context).hasBean("jpaSpecificationService")
                    assertThat(context).hasBean("hibernateModule")
                }
    }

    @Test
    fun dataSearchAutoConfiguration_is_not_loaded_when_SearchConfigurer_not_present() {
        contextRunner.withClassLoader(FilteredClassLoader(SearchConfigurer::class.java))
                .run { context ->
                    assertThat(context).doesNotHaveBean(DataSearchController::class.java)
                    assertThat(context).doesNotHaveBean(DataSearchReactiveController::class.java)
                    assertThat(context).doesNotHaveBean("fieldPathResolver")
                    assertThat(context).doesNotHaveBean("fieldInfoResolver")
                    assertThat(context).doesNotHaveBean("expressionMapper")
                    assertThat(context).doesNotHaveBean("expressionParserVisitorFactory")
                    assertThat(context).doesNotHaveBean("expressionParser")
                    assertThat(context).doesNotHaveBean("dataSearchService")
                    assertThat(context).doesNotHaveBean("dataSearchValidationService")
                    assertThat(context).doesNotHaveBean("dataSearchErrorsFactory")
                    assertThat(context).doesNotHaveBean("entityJoinManager")
                    assertThat(context).doesNotHaveBean("jpaSpecificationService")
                    assertThat(context).doesNotHaveBean("hibernateModule")
                }
    }

    @Test
    fun dataSearchAutoConfiguration_is_loaded_but_hibernateModule_is_not_loaded_when_Hibernate5Module_not_present() {
        contextRunner.withClassLoader(FilteredClassLoader(Hibernate5Module::class.java))
                .run { context ->
                    assertThat(context).hasSingleBean(DataSearchController::class.java)
                    assertThat(context).doesNotHaveBean(DataSearchReactiveController::class.java)
                    assertThat(context).hasBean("fieldPathResolver")
                    assertThat(context).hasBean("fieldInfoResolver")
                    assertThat(context).hasBean("expressionMapper")
                    assertThat(context).hasBean("expressionParserVisitorFactory")
                    assertThat(context).hasBean("expressionParser")
                    assertThat(context).hasBean("dataSearchService")
                    assertThat(context).hasBean("dataSearchValidationService")
                    assertThat(context).hasBean("dataSearchErrorsFactory")
                    assertThat(context).hasBean("entityJoinManager")
                    assertThat(context).hasBean("jpaSpecificationService")
                    assertThat(context).doesNotHaveBean("hibernateModule")
                }
    }
}