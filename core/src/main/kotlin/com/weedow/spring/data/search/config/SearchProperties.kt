package com.weedow.spring.data.search.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * {@link ConfigurationProperties properties} for Spring Data Search.
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.data.search")
data class SearchProperties(
        /**
         * Base path to be used by Spring Data Search to expose data search resources. Default is '/search'.
         */
        val basePath: String = "/search"
)
