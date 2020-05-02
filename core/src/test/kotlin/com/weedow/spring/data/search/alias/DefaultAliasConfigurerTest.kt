package com.weedow.spring.data.search.alias

import com.weedow.spring.data.search.example.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DefaultAliasConfigurerTest {

    @Test
    fun addAliasResolvers() {
        val registry = TestAliasResolverRegistry()

        val defaultAliasConfigurer = DefaultAliasConfigurer()
        defaultAliasConfigurer.addAliasResolvers(registry)

        assertThat(registry.aliasResolvers).hasSize(1)

        val aliasResolver = registry.aliasResolvers[0]

        val entityClass = Person::class.java

        assertThat(aliasResolver.supports(entityClass, getField(entityClass, "firstName"))).isTrue()

        var aliases = aliasResolver.resolve(entityClass, getField(entityClass, "firstName"))
        assertThat(aliases).isEmpty()

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "lastName"))
        assertThat(aliases).isEmpty()

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "addressEntities"))
        assertThat(aliases).containsExactly("address")

        aliases = aliasResolver.resolve(entityClass, getField(entityClass, "jobEntity"))
        assertThat(aliases).containsExactly("job")
    }

    private fun getField(clazz: Class<*>, fieldName: String) = clazz.getDeclaredField(fieldName)

    class TestAliasResolverRegistry : AliasResolverRegistry {
        val aliasResolvers = mutableListOf<AliasResolver>()

        override fun addAliasResolver(aliasResolver: AliasResolver) {
            aliasResolvers.add(aliasResolver)
        }
    }
}