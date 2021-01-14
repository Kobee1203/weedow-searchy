package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.ExpressionUtils
import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.validation.SearchyErrors
import java.net.MalformedURLException
import java.net.URL

/**
 * [SearchyValidator][com.weedow.searchy.validation.SearchyValidator] implementation to check if the field expression value matches
 * a valid [URL][java.net.URL].
 *
 * @param fieldPaths Field paths to validate
 */
class UrlValidator(
    vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: SearchyErrors) {
        try {
            URL(value.toString())
        } catch (e: MalformedURLException) {
            errors.reject("url", "Invalid URL value for expression ''{0}''", ExpressionUtils.format(fieldExpression))
        }
    }

}