package com.weedow.searchy.autoconfigure

import com.weedow.searchy.TestConfiguration
import com.weedow.searchy.config.SearchyConfigurationSupport
import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.controller.reactive.SearchyReactiveController
import com.weedow.searchy.controller.servlet.SearchyServletController
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


internal class SearchyAutoConfigurationTest {

    companion object {
        val BEANS_LIST = listOf(
            "fieldPathResolver",
            "fieldInfoResolver",
            "expressionMapper",
            "expressionParserVisitorFactory",
            "expressionParser",
            "searchyService",
            "searchyValidationService",
            "searchyErrorsFactory",
            "entitySearchService",
            "defaultDtoMapper",
            "dtoConverterService",
            "specificationService",
            "entityJoinManager",
            "searchyDescriptorService",
            "searchAliasResolutionService",
            "searchConversionService"
        )
    }

    /**
     * Test to verify if there is a new Bean and fails in order to force to add the bean name in [BEANS_LIST].
     */
    @Test
    fun check_SearchyConfigurationSupport_beans() {
        val beans = MethodUtils.getMethodsListWithAnnotation(SearchyConfigurationSupport::class.java, Bean::class.java)
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
    fun searchyAutoConfiguration_is_loaded() {
        val contextRunner = WebApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestConfiguration::class.java,
                    WebMvcAutoConfiguration::class.java,
                    SearchyAutoConfiguration::class.java
                )
            )

        contextRunner
            .run { context ->
                assertThat(context).hasSingleBean(SearchyServletController::class.java)
                assertThat(context).doesNotHaveBean(SearchyReactiveController::class.java)
                BEANS_LIST.forEach { assertThat(context).hasBean(it) }
            }
    }

    @Test
    fun searchyAutoConfiguration_is_loaded_with_reactive_controller() {
        val contextRunner = ReactiveWebApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestConfiguration::class.java,
                    WebFluxAutoConfiguration::class.java,
                    SearchyAutoConfiguration::class.java
                )
            )

        contextRunner
            .run { context ->
                assertThat(context).hasSingleBean(SearchyReactiveController::class.java)
                assertThat(context).doesNotHaveBean(SearchyServletController::class.java)
                BEANS_LIST.forEach { assertThat(context).hasBean(it) }
            }
    }

    @Test
    fun searchyAutoConfiguration_is_not_loaded_when_SearchyConfigurer_not_present() {
        val contextRunner = WebApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    TestConfiguration::class.java,
                    WebMvcAutoConfiguration::class.java,
                    SearchyAutoConfiguration::class.java
                )
            )
            .withClassLoader(FilteredClassLoader(SearchyConfigurer::class.java))

        contextRunner
            .run { context ->
                assertThat(context).doesNotHaveBean(SearchyServletController::class.java)
                assertThat(context).doesNotHaveBean(SearchyReactiveController::class.java)
                BEANS_LIST.forEach { assertThat(context).doesNotHaveBean(it) }
            }
    }

}