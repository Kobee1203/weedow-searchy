package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors

/**
 * [DataSearchValidator][com.weedow.spring.data.search.validation.DataSearchValidator] implementation to check if the field expression value is between the specified [minValue] and [maxValue].
 */
class RangeValidator(
        private val minValue: Number,
        private val maxValue: Number,
        vararg fieldPaths: String
) : AbstractNumberValidator(*fieldPaths) {

    override fun doValidate(value: Number, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        if (compare(value, minValue) < 0 || compare(value, maxValue) > 0) {
            errors.reject("range", "Invalid number value for expression ''{0}''. Must between ''{1}'' and ''{2}''.", ExpressionUtils.format(fieldExpression), minValue, maxValue)
        }
    }

}