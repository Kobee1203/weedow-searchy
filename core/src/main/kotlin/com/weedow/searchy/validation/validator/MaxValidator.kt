package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.ExpressionUtils
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.validation.SearchyErrors

/**
 * [SearchyValidator][com.weedow.searchy.validation.SearchyValidator] implementation to check if the field expression value is
 * less or equals to the specified [maxValue].
 *
 * @param maxValue Maximum Number allowed
 * @param fieldPaths Field paths to validate
 */
class MaxValidator(
    private val maxValue: Number,
    vararg fieldPaths: String
) : AbstractNumberValidator(*fieldPaths) {

    override fun doValidate(value: Number, fieldExpression: FieldExpression, errors: SearchyErrors) {
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