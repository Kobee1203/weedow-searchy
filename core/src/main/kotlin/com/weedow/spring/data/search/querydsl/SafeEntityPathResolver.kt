package com.weedow.spring.data.search.querydsl

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.PathBuilderFactory
import com.weedow.spring.data.search.utils.klogger
import org.springframework.data.querydsl.EntityPathResolver
import org.springframework.data.querydsl.SimpleEntityPathResolver

class SafeEntityPathResolver(
        querySuffix: String,
) : SimpleEntityPathResolver(querySuffix), EntityPathResolver {

    private val pathBuilderFactory = PathBuilderFactory(querySuffix)

    companion object {
        private val log by klogger()

        val INSTANCE = SafeEntityPathResolver("")
    }

    override fun <T : Any?> createPath(domainClass: Class<T>): EntityPath<T> {
        return try {
            super.createPath(domainClass)
        } catch (e: IllegalArgumentException) {
            log.debug("Could not create an EntityPath from the Query Class: {} -> Creating an EntityPath instance directly from the given type {}", e.message, domainClass.name)
            pathBuilderFactory.create(domainClass)
        }
    }
}