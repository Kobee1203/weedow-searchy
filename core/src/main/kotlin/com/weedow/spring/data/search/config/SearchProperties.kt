package com.weedow.spring.data.search.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * [Properties][ConfigurationProperties] for Spring Data Search.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.data.search")
data class SearchProperties(
        /**
         * Base path to be used by Spring Data Search to expose data search resources. Default is '/search'.
         */
        val basePath: String = DEFAULT_BASE_PATH,

        @NestedConfigurationProperty
        val defaultAliasResolver: DefaultAliasResolver = DefaultAliasResolver()
) {
    companion object {
        /** Default Base Path */
        const val DEFAULT_BASE_PATH = "/search"
    }
}

@ConstructorBinding
data class DefaultAliasResolver(
        /** Comma-separated list of field suffixes to be removed in order to create a field's alias from the com.weedow.spring.data.search.config.DefaultAliasResolver. Default is `Entity,Entities` */
        val fieldSuffixes: List<String> = FIELD_SUFFIXES
) {

    companion object {
        /**
         * Default Field suffixes to be removed
         */
        private val FIELD_SUFFIXES = listOf(
                "Entity",
                "Entities"
        )
    }
}
