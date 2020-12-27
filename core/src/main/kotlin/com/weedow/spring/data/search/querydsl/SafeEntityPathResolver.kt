package com.weedow.spring.data.search.querydsl

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.PathBuilderFactory
import com.weedow.spring.data.search.utils.klogger
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.querydsl.SimpleEntityPathResolver

/**
 * Simple implementation of EntityPathResolver to lookup a query class by reflection and using the static field of the same type.
 *
 * If the query class is not found, a new [PathBuilder][com.querydsl.core.types.dsl.PathBuilder] instance is created for the given type.
 *
 * @param querySuffix String
 */
class SafeEntityPathResolver(
    querySuffix: String
) : SimpleEntityPathResolver(querySuffix), EntityPathResolver {

    private val pathBuilderFactory = PathBuilderFactory()

    companion object {
        private val log by klogger()

        val INSTANCE = SafeEntityPathResolver("")
    }

    override fun <T : Any?> createPath(domainClass: Class<T>): EntityPath<T> {
        return try {
            super.createPath(domainClass)
        } catch (e: IllegalArgumentException) {
            log.debug(
                "Could not create an EntityPath from the Query Class: {} -> Creating an EntityPath instance directly from the given type {}",
                e.message,
                domainClass.name
            )
            pathBuilderFactory.create(domainClass)
        }
    }
}