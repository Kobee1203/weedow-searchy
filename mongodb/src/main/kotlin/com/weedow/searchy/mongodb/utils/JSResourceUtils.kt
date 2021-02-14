package com.weedow.searchy.mongodb.utils

import org.springframework.core.io.Resource
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * Utils class for JS [Resource]s.
 */
internal object JSResourceUtils {

    const val SYS_PROP_JS_DEBUG = "weedow_searchy_mongodb_js_debug"

    /**
     * Load the given Resource representing a JS content and returns the String representation, optimized or not according to the value of System property [SYS_PROP_JS_DEBUG].
     *
     * * If the system property [SYS_PROP_JS_DEBUG] is `true`, the given JS resource is not optimized and the JS content is returned directly.
     * * If the system property [SYS_PROP_JS_DEBUG] is `false`, the given JS resource is [optimized][optimize] before being returned.
     */
    fun load(jsResource: Resource): String {
        return if (System.getProperty(SYS_PROP_JS_DEBUG, "false").toBoolean()) {
            String(jsResource.inputStream.readAllBytes(), StandardCharsets.UTF_8)
        } else {
            optimize(jsResource.inputStream)
        }
    }

    /**
     * Optimize the input representing a JS content:
     * * Minifies the input
     * * Remove logging code
     *
     * @param `in` Input representing a JS content to be optimized
     */
    private fun optimize(input: InputStream): String {
        val output = ByteArrayOutputStream()
        JSMin(input, output).jsmin()
        return output.toString()
            .replace("var log=function(msg){printjson(msg);}", "")
            .replace(Regex("log\\([^\\;]+;"), "")
    }

}