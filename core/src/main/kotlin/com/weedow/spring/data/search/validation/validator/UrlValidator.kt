package com.weedow.spring.data.search.validation.validator

import com.weedow.spring.data.search.expression.ExpressionUtils
import com.weedow.spring.data.search.expression.FieldExpression
import com.weedow.spring.data.search.validation.DataSearchErrors
import java.net.MalformedURLException
import java.net.URL

/**
 * [DataSearchValidator][com.weedow.spring.data.search.validation.DataSearchValidator] implementation to check if the field expression value matches a valid [URL][java.net.URL].
 */
class UrlValidator(
        vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    override fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: DataSearchErrors) {
        try {
            URL(value.toString())
        } catch (e: MalformedURLException) {
            errors.reject("url", "Invalid URL value for expression ''{0}''", ExpressionUtils.format(fieldExpression))
        }
    }

}