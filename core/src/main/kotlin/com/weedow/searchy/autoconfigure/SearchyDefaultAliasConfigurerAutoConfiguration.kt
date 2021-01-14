package com.weedow.searchy.autoconfigure

import com.weedow.searchy.alias.AliasResolver
import com.weedow.searchy.alias.AliasResolverRegistry
import com.weedow.searchy.config.SearchyConfigurer
import com.weedow.searchy.config.SearchyProperties
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Field

/**
 * Auto-Configuration to register default alias resolvers.
 */
@Configuration
@ConditionalOnClass(SearchyConfigurer::class)
@EnableConfigurationProperties(SearchyProperties::class)
class SearchyDefaultAliasConfigurerAutoConfiguration(
    private val searchyProperties: SearchyProperties
) : SearchyConfigurer {

    override fun addAliasResolvers(registry: AliasResolverRegistry) {
        registry.addAliasResolver(DefaultAliasResolver(searchyProperties.defaultAliasResolver.fieldSuffixes))
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