package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.utils.NullValue
import com.weedow.spring.data.search.validation.DataSearchErrors

/**
 * [DataSearchValidator][com.weedow.spring.data.search.validation.DataSearchValidator] implementation to check if the field expression value is not `null`.
 *
 * The comparison is made with the [NullValue] object or [NullValue.NULL_VALUE] constant.
 *
 * @param fieldPaths Field paths to validate
 */
class NotNullValidator(
    vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        if (value == NullValue || value == NullValue.NULL_VALUE) {
            errors.reject("not-null", "Invalid value for expression ''{0}''. Must not be null.", ExpressionUtils.format(fieldExpression))
        }
    }

}