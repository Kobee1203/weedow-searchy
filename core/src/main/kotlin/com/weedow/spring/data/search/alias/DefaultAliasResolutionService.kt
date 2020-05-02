package com.weedow.spring.data.search.alias

import org.apache.commons.lang3.reflect.FieldUtils
import java.util.*

class DefaultAliasResolutionService : ConfigurableAliasResolutionService {

    private val aliasResolvers: MutableList<AliasResolver> = ArrayList()
    private val processedEntityClasses: MutableList<Class<*>> = ArrayList()
    private val aliases: MutableMap<String, String> = HashMap()

    override fun addAliasResolver(aliasResolver: AliasResolver) {
        aliasResolvers.add(aliasResolver)
    }

    override fun resolve(parentClass: Class<*>, alias: String): String {
        if (!aliasResolvers.isNullOrEmpty() && !processedEntityClasses.contains(parentClass)) {
            initAliases(parentClass)
            processedEntityClasses.add(parentClass)
        }
        return aliases.getOrDefault(parentClass.simpleName + "." + alias, alias)
    }

    private fun initAliases(entityClass: Class<*>) {
        for (field in FieldUtils.getAllFieldsList(entityClass)) {
            val fieldName = field.name
            aliasResolvers.forEach { aliasResolver ->
                if (aliasResolver.supports(entityClass, field)) {
                    val aliases: List<String?> = aliasResolver.resolve(entityClass, field)
                    if (!aliases.isNullOrEmpty()) {
                        aliases.forEach { alias -> this.aliases[entityClass.simpleName + "." + alias] = fieldName }
                    }
                }
            }
        }
    }
}