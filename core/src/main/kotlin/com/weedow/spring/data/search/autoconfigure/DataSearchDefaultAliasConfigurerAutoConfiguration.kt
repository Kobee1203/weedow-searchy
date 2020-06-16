package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.alias.AliasResolver
import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.config.SearchConfigurer
import org.apache.commons.lang3.StringUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Field

/**
 * Auto-Configuration to register default alias resolvers.
 */
@Configuration
@ConditionalOnClass(SearchConfigurer::class)
class DataSearchDefaultAliasConfigurerAutoConfiguration : SearchConfigurer {

    override fun addAliasResolvers(registry: AliasResolverRegistry) {
        registry.addAliasResolver(DefaultAliasResolver())
    }

    /**
     * Default Alias Resolver that create a new alias for all fields ending with 'Entity' or 'Entities'.
     *
     * The resolved alias is the field name without 'Entity' and 'Entities'.
     */
    class DefaultAliasResolver : AliasResolver {

        companion object {
            /**
             * Field suffixes to be removed
             */
            private val FIELD_SUFFIXES = listOf(
                    "Entity",
                    "Entities"
            )
        }

        override fun supports(entityClass: Class<*>, field: Field): Boolean {
            return true
        }

        override fun resolve(entityClass: Class<*>, field: Field): List<String> {
            val fieldName = field.name
            for (fieldSuffix in FIELD_SUFFIXES) {
                if (fieldName.endsWith(fieldSuffix)) {
                    return listOf(StringUtils.substringBefore(fieldName, fieldSuffix))
                }
            }
            return emptyList()
        }
    }
}