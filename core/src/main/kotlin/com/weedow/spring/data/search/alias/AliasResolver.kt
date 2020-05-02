package com.weedow.spring.data.search.alias

import java.lang.reflect.Field

interface AliasResolver {

    fun supports(entityClass: Class<*>, field: Field): Boolean

    fun resolve(entityClass: Class<*>, field: Field): List<String>

}