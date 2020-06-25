package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors
import com.weedow.spring.data.search.validation.validator.EmailValidator.Companion.DEFAULT_PATTERN

/**
 * [DataSearchValidator][com.weedow.spring.data.search.validation.DataSearchValidator] implementation to check if the field expression value matches the email format.
 *
 * It's possible to override the [default pattern][DEFAULT_PATTERN]. To do this, pass the new value to the [pattern] parameter.
 * ```
 * EmailValidator("email", "otherEmail", pattern = "{CUSTOM_PATTERN}")
 * ```
 */
class EmailValidator(
        vararg fieldPaths: String,
        private val pattern: String = DEFAULT_PATTERN
) : AbstractFieldPathValidator(*fieldPaths) {

    private val regex = Regex(pattern)

    companion object {
        private const val DEFAULT_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
    }

    override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        if (!value.toString().matches(regex)) {
            errors.reject("email", "Invalid email value for expression ''{0}''.", ExpressionUtils.format(fieldExpression))
        }
    }

}

