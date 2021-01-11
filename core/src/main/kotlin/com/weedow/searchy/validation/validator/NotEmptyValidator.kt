package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.validation.SearchyErrors
import com.weedow.searchy.validation.SearchyValidator
import com.weedow.searchy.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_CODE
import com.weedow.searchy.validation.validator.NotEmptyValidator.Companion.DEFAULT_ERROR_MESSAGE

/**
 * [SearchyValidator][com.weedow.searchy.validation.SearchyValidator] implementation to check if there is at least one field expression.
 *
 * @param errorCode Default is [DEFAULT_ERROR_CODE]
 * @param errorMessage Default is [DEFAULT_ERROR_MESSAGE]
 */
class NotEmptyValidator(
    private val errorCode: String = DEFAULT_ERROR_CODE,
    private val errorMessage: String = DEFAULT_ERROR_MESSAGE
) : SearchyValidator {

    companion object {
        /** Default error code */
        const val DEFAULT_ERROR_CODE = "not-empty"

        /** Default error message */
        const val DEFAULT_ERROR_MESSAGE = "The search must contain at least one field expression."
    }

    override fun validate(fieldExpressions: Collection<FieldExpression>, errors: SearchyErrors) {
        if (fieldExpressions.isEmpty()) {
            errors.reject(errorCode, errorMessage)
        }
    }

}