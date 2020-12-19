package com.weedow.spring.data.search.expression

import com.weedow.spring.data.search.fieldpath.FieldPathInfo
import com.weedow.spring.data.search.fieldpath.FieldPathResolver
import com.weedow.spring.data.search.utils.Keyword.CURRENT_DATE
import com.weedow.spring.data.search.utils.Keyword.CURRENT_DATE_TIME
import com.weedow.spring.data.search.utils.Keyword.CURRENT_TIME
import com.weedow.spring.data.search.utils.NullValue
import com.weedow.spring.data.search.utils.NullValue.NULL_VALUE
import org.springframework.core.convert.ConversionService
import java.util.stream.Collectors

/**
 * Default [ExpressionResolver] implementation.
 *
 * @param fieldPathResolver [FieldPathResolver]
 * @param conversionService [ConversionService]
 */
class ExpressionResolverImpl(
    private val fieldPathResolver: FieldPathResolver,
    private val conversionService: ConversionService
) : ExpressionResolver {

    override fun resolveExpression(
        rootClass: Class<*>,
        fieldPath: String,
        fieldValues: List<String>,
        operator: Operator,
        negated: Boolean
    ): Expression {
        val fieldPathInfo = toFieldKey(rootClass, fieldPath)
        val values = toFieldValues(fieldPathInfo, fieldValues)

        val fieldInfo = FieldInfo(fieldPathInfo.fieldPath, fieldPathInfo.fieldName, fieldPathInfo.parentClass)
        val expression = when (operator) {
            Operator.EQUALS -> ExpressionUtils.equals(fieldInfo, values.first())
            Operator.MATCHES -> ExpressionUtils.matches(fieldInfo, values.first())
            Operator.IMATCHES -> ExpressionUtils.imatches(fieldInfo, values.first())
            Operator.LESS_THAN -> ExpressionUtils.lessThan(fieldInfo, values.first())
            Operator.LESS_THAN_OR_EQUALS -> ExpressionUtils.lessThanOrEquals(fieldInfo, values.first())
            Operator.GREATER_THAN -> ExpressionUtils.greaterThan(fieldInfo, values.first())
            Operator.GREATER_THAN_OR_EQUALS -> ExpressionUtils.greaterThanOrEquals(fieldInfo, values.first())
            Operator.IN -> ExpressionUtils.`in`(fieldInfo, values)
        }

        return if (negated) ExpressionUtils.not(expression) else expression
    }

    private fun toFieldKey(rootClass: Class<*>, fieldPath: String): FieldPathInfo {
        return fieldPathResolver.resolveFieldPath(rootClass, fieldPath)
    }

    private fun toFieldValues(fieldPathInfo: FieldPathInfo, fieldValues: List<String>): List<Any> {
        return fieldValues.stream()
            .map { fieldValue -> convert(fieldValue, fieldPathInfo.fieldClass) }
            .collect(Collectors.toList())
    }

    private fun convert(value: String, clazz: Class<*>): Any {
        return when {
            CURRENT_DATE.equals(value, ignoreCase = true) -> CURRENT_DATE
            CURRENT_TIME.equals(value, ignoreCase = true) -> CURRENT_TIME
            CURRENT_DATE_TIME.equals(value, ignoreCase = true) -> CURRENT_DATE_TIME
            NULL_VALUE.equals(value, ignoreCase = true) -> NullValue
            else -> conversionService.convert(value, clazz)!!
        }
    }

}
