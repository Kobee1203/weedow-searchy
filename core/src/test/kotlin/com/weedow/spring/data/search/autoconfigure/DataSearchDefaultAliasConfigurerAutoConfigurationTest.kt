package com.weedow.spring.data.search.autoconfigure

import com.weedow.spring.data.search.alias.AliasResolver
import com.weedow.spring.data.search.alias.AliasResolverRegistry
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.config.DefaultAliasResolver
import com.weedow.spring.data.search.config.SearchProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DataSearchDefaultAliasConfigurerAutoConfigurationTest {

    @Test
    fun add_alias_resolvers() {
        val registry = TestAliasResolverRegistry()

        val defaultAliasConfigurer = DataSearchDefaultAliasConfigurerAutoConfiguration(SearchProperties())
        defaultAliasConfigurer.addAliasResolvers(registry)

        assertThat(registry.aliasResolvers).hasSize(1)

        val aliasResolver = registry.aliasResolvers[0]

        val entityClass = Person::class.java

        assertThat(aliasResolver.supports(entityClass, getField(entityClass, "firstName"))).isTrue

        var aliases = aliasResolver.resolve(entityClass, getField(entityClass, "firstName"))
        assertThat(aliases).isEmpty()

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "lastName"))
        assertThat(aliases).isEmpty()

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "addressEntities"))
        assertThat(aliases).containsExactly("address")

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "jobEntity"))
        assertThat(aliases).containsExactly("job")
    }

    @Test
    fun add_default_alias_resolver_with_custom_field_suffixes() {
        val registry = TestAliasResolverRegistry()

        val defaultAliasConfigurer =
            DataSearchDefaultAliasConfigurerAutoConfiguration(SearchProperties(defaultAliasResolver = DefaultAliasResolver(listOf("Name", "Names"))))
        defaultAliasConfigurer.addAliasResolvers(registry)

        assertThat(registry.aliasResolvers).hasSize(1)

        val aliasResolver = registry.aliasResolvers[0]

        val entityClass = Person::class.java

        assertThat(aliasResolver.supports(entityClass, getField(entityClass, "firstName"))).isTrue

        var aliases = aliasResolver.resolve(entityClass, getField(entityClass, "firstName"))
        assertThat(aliases).containsExactly("first")

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "lastName"))
        assertThat(aliases).containsExactly("last")

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "nickNames"))
        assertThat(aliases).containsExactly("nick")
    }

    private fun getField(clazz: Class<*>, fieldName: String) = clazz.getDeclaredField(fieldName)

    class TestAliasResolverRegistry : AliasResolverRegistry {
        val aliasResolvers = mutableListOf<AliasResolver>()

        override fun addAliasResolver(aliasResolver: AliasResolver) {
            aliasResolvers.add(aliasResolver)
        }
    }
}