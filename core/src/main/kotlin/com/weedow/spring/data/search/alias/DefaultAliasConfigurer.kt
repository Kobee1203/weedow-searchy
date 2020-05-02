package com.weedow.spring.data.search.alias

import com.weedow.spring.data.search.config.SearchConfigurer
import org.apache.commons.lang3.StringUtils
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Field

@Configuration
class DefaultAliasConfigurer : SearchConfigurer {

    override fun addAliasResolvers(registry: AliasResolverRegistry) {
        registry.addAliasResolver(DefaultAliasResolver())
    }

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