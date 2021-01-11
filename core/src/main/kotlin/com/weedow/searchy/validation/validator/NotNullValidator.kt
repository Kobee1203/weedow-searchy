package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.ExpressionUtils
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.utils.NullValue
import com.weedow.searchy.validation.SearchyErrors

/**
 * [SearchyValidator][com.weedow.searchy.validation.SearchyValidator] implementation to check if the field expression value is not `null`.
 *
 * The comparison is made with the [NullValue] object or [NullValue.NULL_VALUE] constant.
 *
 * @param fieldPaths Field paths to validate
 */
class NotNullValidator(
    vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: SearchyErrors) {
        if (value == NullValue || value == NullValue.NULL_VALUE) {
            errors.reject("not-null", "Invalid value for expression ''{0}''. Must not be null.", ExpressionUtils.format(fieldExpression))
        }
    }

}