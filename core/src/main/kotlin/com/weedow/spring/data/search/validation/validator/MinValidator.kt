package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors

/**
 * [DataSearchValidator][com.weedow.spring.data.search.validation.DataSearchValidator] implementation to check if the field expression value is
 * greater or equals to the specified [minValue].
 *
 * @param minValue Minimum Number allowed
 * @param fieldPaths Field paths to validate
 */
class MinValidator(
    private val minValue: Number,
    vararg fieldPaths: String
) : AbstractNumberValidator(*fieldPaths) {

    override fun doValidate(value: Number, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        if (compare(value, minValue) < 0) {
            errors.reject(
                "min",
                "Invalid number value for expression ''{0}''. Must greater or equals to ''{1}''.",
                ExpressionUtils.format(fieldExpression),
                minValue
            )
        }
    }

}