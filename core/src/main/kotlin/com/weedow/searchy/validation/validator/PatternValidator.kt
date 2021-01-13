package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.ExpressionUtils
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.validation.SearchyErrors

/**
 * [SearchyValidator][com.weedow.searchy.validation.SearchyValidator] implementation to check if the field expression value matches the specified [pattern].
 *
 * @param pattern Pattern String used to check if the value matches this given pattern
 * @param fieldPaths Field paths to validate
 */
class PatternValidator(
    private val pattern: String,
    vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    private val regex = Regex(pattern)

    override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: SearchyErrors) {
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