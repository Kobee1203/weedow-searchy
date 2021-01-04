package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors

/**
 * [DataSearchValidator][com.weedow.spring.data.search.validation.DataSearchValidator] implementation to check if the field expression value is
 * less or equals to the specified [maxValue].
 *
 * @param maxValue Maximum Number allowed
 * @param fieldPaths Field paths to validate
 */
class MaxValidator(
    private val maxValue: Number,
    vararg fieldPaths: String
) : AbstractNumberValidator(*fieldPaths) {

    override fun doValidate(value: Number, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        if (compare(value, maxValue) > 0) {
            errors.reject(
                "max",
                "Invalid number value for expression ''{0}''. Must less or equals to ''{1}''.",
                ExpressionUtils.format(fieldExpression),
                maxValue
            )
        }
    }

}