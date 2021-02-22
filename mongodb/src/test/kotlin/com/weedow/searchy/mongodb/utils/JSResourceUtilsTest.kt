package com.weedow.searchy.mongodb.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.core.io.DefaultResourceLoader
import java.nio.charset.StandardCharsets
import java.util.stream.Stream

internal class JSResourceUtilsTest {

    @ParameterizedTest
    @MethodSource("debug_mode_with_result_resource")
    fun optimize(debugMode: Boolean, resultResource: String) {
        System.setProperty(JSResourceUtils.SYS_PROP_JS_DEBUG, debugMode.toString())

        val jsResource = DefaultResourceLoader().getResource("classpath:map_contains_value.js")
        val result = JSResourceUtils.load(jsResource)

        assertThat(result)
            .isEqualTo(String(javaClass.getResourceAsStream(resultResource).readAllBytes(), StandardCharsets.UTF_8))
    }

    companion object {
        @AfterAll
        @JvmStatic
        fun afterAll() {
            System.clearProperty(JSResourceUtils.SYS_PROP_JS_DEBUG)
        }

        @JvmStatic
        @Suppress("unused")
        private fun debug_mode_with_result_resource(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(true, "/map_contains_value.js"),
                Arguments.of(false, "/map_contains_value.optimized.js")
            )
        }
    }
}