package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors

/**
 * [DataSearchValidator][com.weedow.spring.data.search.validation.DataSearchValidator] implementation to check if the field expression value matches the specified [pattern].
 *
 * @param pattern Pattern String used to check if the value matches this given pattern
 * @param fieldPaths Field paths to validate
 */
class PatternValidator(
    private val pattern: String,
    vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    private val regex = Regex(pattern)

    override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        if (!value.toString().matches(regex)) {
            errors.reject(
                "pattern",
                "Invalid value for expression ''{0}''. Must match ''{1}''.",
                ExpressionUtils.format(fieldExpression),
                regex.pattern
            )
        }
    }

}