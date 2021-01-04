package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.TestConfiguration
import com.weedow.spring.data.search.config.SearchConfigurationSupport
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.controller.reactive.DataSearchReactiveController
import com.weedow.spring.data.search.controller.servlet.DataSearchController
import org.apache.commons.lang3.reflect.MethodUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.test.context.FilteredClassLoader
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.context.annotation.Bean


internal class DataSearchAutoConfigurationTest {

    companion object {
        val BEANS_LIST = listOf(
            "fieldPathResolver",
            "fieldInfoResolver",
            "expressionMapper",
            "expressionParserVisitorFactory",
            "expressionParser",
            "dataSearchService",
            "dataSearchValidationService",
            "dataSearchErrorsFactory",
            "entitySearchService",
            "defaultDtoMapper",
            "dtoConverterService",
            "specificationService",
            "entityJoinManager",
            "searchDescriptorService",
            "searchAliasResolutionService",
            "searchConversionService"
        )
    }

    /**
     * Test to verify if there is a new Bean and fails in order to force to add the bean name in [BEANS_LIST].
     */
    @Test
    fun check_SearchConfigurationSupport_beans() {
        val beans = MethodUtils.getMethodsListWithAnnotation(SearchConfigurationSupport::class.java, Bean::class.java)
            .flatMap {
                val beanAnnotation = it.getAnnotation(Bean::class.java)
                when {
                    beanAnnotation.value.isNotEmpty() -> beanAnnotation.value.toList()
                    beanAnnotation.name.isNotEmpty() -> beanAnnotation.name.toList()
                    else -> listOf(it.name)
                }
            }

        assertThat(beans).hasSize(BEANS_LIST.size)
        assertThat(beans).containsExactlyInAnyOrder(*BEANS_LIST.toTypedArray())
    }

    @Test
    fun dataSearchAutoConfiguration_is_loaded() {
        val contextRunner = WebApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestConfiguration::class.java,
                    WebMvcAutoConfiguration::class.java,
                    DataSearchAutoConfiguration::class.java
                )
            )

        contextRunner
            .run { context ->
                assertThat(context).hasSingleBean(DataSearchController::class.java)
                assertThat(context).doesNotHaveBean(DataSearchReactiveController::class.java)
                BEANS_LIST.forEach { assertThat(context).hasBean(it) }
            }
    }

    @Test
    fun dataSearchAutoConfiguration_is_loaded_with_reactive_controller() {
        val contextRunner = ReactiveWebApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestConfiguration::class.java,
                    WebFluxAutoConfiguration::class.java,
                    DataSearchAutoConfiguration::class.java
                )
            )

        contextRunner
            .run { context ->
                assertThat(context).hasSingleBean(DataSearchReactiveController::class.java)
                assertThat(context).doesNotHaveBean(DataSearchController::class.java)
                BEANS_LIST.forEach { assertThat(context).hasBean(it) }
            }
    }

    @Test
    fun dataSearchAutoConfiguration_is_not_loaded_when_SearchConfigurer_not_present() {
        val contextRunner = WebApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestConfiguration::class.java,
                    WebMvcAutoConfiguration::class.java,
                    DataSearchAutoConfiguration::class.java
                )
            )
            .withClassLoader(FilteredClassLoader(SearchConfigurer::class.java))

        contextRunner
            .run { context ->
                assertThat(context).doesNotHaveBean(DataSearchController::class.java)
                assertThat(context).doesNotHaveBean(DataSearchReactiveController::class.java)
                BEANS_LIST.forEach { assertThat(context).doesNotHaveBean(it) }
            }
    }

}