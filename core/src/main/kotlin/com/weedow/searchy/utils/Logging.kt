package com.weedow.searchy.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

/**
 * Utility method to declare a new logger in a Class.
 *
 * * Declare a logger in a `Class`:
 * ```
 * class MyClass {
 *   companion object {
 *     private val log by klogger()
 *   }
 * }
 * ```
 *
 * * Declare a logger in an `object`:
 * ```
 * object MyObject {
 *   private val log by klogger()
 * }
 * ```
 */
fun <R : Any> R.klogger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass)) }
}

private fun <T : Any> unwrapCompanionClass(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}