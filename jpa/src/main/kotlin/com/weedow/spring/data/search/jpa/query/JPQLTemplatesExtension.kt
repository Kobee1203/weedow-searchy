package com.weedow.spring.data.search.jpa.query

import com.querydsl.core.types.Operator
import com.querydsl.core.types.Ops
import com.querydsl.core.types.Templates
import com.querydsl.jpa.JPQLTemplates
import org.apache.commons.lang3.reflect.MethodUtils
import java.lang.reflect.Method

/*
class JPQLTemplatesExtension(
        escape: Char = JPQLTemplates.DEFAULT_ESCAPE,
        queryHandler: QueryHandler = DefaultQueryHandler.DEFAULT
) : JPQLTemplates(escape, queryHandler) {

    init {
        addMissingTemplates()
    }
}
*/

fun JPQLTemplates.addMissingTemplates() {
    val addMethod = getMethod(this::class.java, "add", Operator::class.java, String::class.java)
    // val addWithPreMethod = getMethod(this::class.java, "add", Operator::class.java, String::class.java, Int::class.java)
    // val addOpsMethod = getMethod(this::class.java, "add", Map::class.java)

    // map
    addMethod.invoke(this, Ops.MAP_IS_EMPTY, "{0} is empty")
    //addMethod.invoke(this, Ops.CONTAINS_KEY, "key({0}) = {1}")
    //addMethod.invoke(this, Ops.CONTAINS_VALUE, "value({0}) = {1}")
}

private fun getMethod(clazz: Class<out Templates>, methodName: String, vararg parameterTypes: Class<*>): Method {
    val method = MethodUtils.getMatchingMethod(clazz, methodName, *parameterTypes)
    method.isAccessible = true
    return method
}