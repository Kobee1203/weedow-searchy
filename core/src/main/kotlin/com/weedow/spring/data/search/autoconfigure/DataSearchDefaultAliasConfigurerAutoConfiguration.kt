package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.alias.AliasResolver
import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.config.SearchConfigurer
import com.weedow.spring.data.search.config.SearchProperties
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Field

/**
 * Auto-Configuration to register default alias resolvers.
 */
@Configuration
@ConditionalOnClass(SearchConfigurer::class)
@EnableConfigurationProperties(SearchProperties::class)
class DataSearchDefaultAliasConfigurerAutoConfiguration(
    private val searchProperties: SearchProperties
) : SearchConfigurer {

    override fun addAliasResolvers(registry: AliasResolverRegistry) {
        registry.addAliasResolver(DefaultAliasResolver(searchProperties.defaultAliasResolver.fieldSuffixes))
    }

    /**
     * Default Alias Resolver that creates a new alias for all fields ending with one of the given [fieldSuffixes].
     *
     * The resolved alias is the field name without one of the given [fieldSuffixes].
     *
     * @param fieldSuffixes List of field suffixes to be removed in order to create a field's alias
     */
    class DefaultAliasResolver(
        private val fieldSuffixes: List<String>
    ) : AliasResolver {

        override fun supports(entityClass: Class<*>, field: Field): Boolean {
            return true
        }

        override fun resolve(entityClass: Class<*>, field: Field): List<String> {
            val fieldName = field.name
            for (fieldSuffix in fieldSuffixes) {
                if (fieldName.endsWith(fieldSuffix)) {
                    return listOf(StringUtils.substringBefore(fieldName, fieldSuffix))
                }
            }
            return emptyList()
        }
    }
}