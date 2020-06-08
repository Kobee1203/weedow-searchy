package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.field.FieldInfo
import com.weedow.spring.data.search.field.FieldPathInfo
import com.weedow.spring.data.search.field.FieldPathResolver
import com.weedow.spring.data.search.utils.NullValue
import com.weedow.spring.data.search.utils.klogger
import org.springframework.core.convert.ConversionService
import java.util.*
import java.util.stream.Collectors

class ExpressionMapperImpl(
        private val fieldPathResolver: FieldPathResolver,
        private val conversionService: ConversionService
) : ExpressionMapper {

    companion object {
        private val log by klogger()
    }

    init {
        if (log.isDebugEnabled) log.debug("Initialized ExpressionMapper: {}", this)
    }

    override fun <T> toExpression(params: Map<String, List<String>>, rootClass: Class<T>): RootExpression<T> {
        val expressions: MutableList<Expression> = ArrayList()
        params.forEach { (paramName, paramValues) ->
            if (paramName != "query") {
                val fieldPathInfo = toFieldKey(rootClass, paramName)
                val fieldValues = toFieldValues(fieldPathInfo, paramValues)
                val fieldInfo = FieldInfo(fieldPathInfo.fieldPath, fieldPathInfo.parentClass, fieldPathInfo.field, fieldPathInfo.fieldClass)
                val expression = if (fieldValues.size == 1) ExpressionUtils.equals(fieldInfo, fieldValues[0]!!) else ExpressionUtils.`in`(fieldInfo, fieldValues)
                expressions.add(expression)
            } else {
                // Process special 'query' parameter
                // expressions.add(queryExpression)
            }
        }
        return RootExpressionImpl(*expressions.toTypedArray())
    }

    private fun toFieldKey(rootClass: Class<*>, fieldPath: String): FieldPathInfo {
        return fieldPathResolver.resolveFieldPath(rootClass, fieldPath)
    }

    private fun toFieldValues(fieldPathInfo: FieldPathInfo, fieldValues: List<String>): List<*> {
        return fieldValues.stream()
                .map { fieldValue -> convert(fieldValue, fieldPathInfo.fieldClass) }
                .collect(Collectors.toList())
    }

    private fun convert(value: String, clazz: Class<*>): Any? {
        return if (!NullValue.NULL_VALUE.equals(value, ignoreCase = true)) conversionService.convert(value, clazz) else NullValue
    }

}