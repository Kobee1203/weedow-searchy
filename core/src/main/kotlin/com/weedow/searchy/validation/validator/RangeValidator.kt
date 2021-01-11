package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.ExpressionUtils
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.validation.SearchyErrors

/**
 * [SearchyValidator][com.weedow.searchy.validation.SearchyValidator] implementation to check if the field expression value is
 * between the specified [minValue] and [maxValue].
 *
 * @param minValue Minimum Number allowed
 * @param maxValue Maximum Number allowed
 * @param fieldPaths Field paths to validate
 */
class RangeValidator(
    private val minValue: Number,
    private val maxValue: Number,
    vararg fieldPaths: String
) : AbstractNumberValidator(*fieldPaths) {

    override fun doValidate(value: Number, fieldExpression: FieldExpression, errors: SearchyErrors) {
        if (compare(value, minValue) < 0 || compare(value, maxValue) > 0) {
            errors.reject(
                "range",
                "Invalid number value for expression ''{0}''. Must between ''{1}'' and ''{2}''.",
                ExpressionUtils.format(fieldExpression),
                minValue,
                maxValue
            )
        }
    }

}