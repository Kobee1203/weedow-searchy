package com.weedow.searchy.validation.validator

import com.weedow.searchy.expression.FieldExpression
import com.weedow.searchy.validation.SearchyErrors

/**
 * [SearchyValidator][com.weedow.searchy.validation.SearchyValidator] implementation to check if all specified required [fieldPaths] are present.
 *
 * The validator iterates over the field expressions and compare the related `fieldPath` with the required [fieldPaths].
 *
 * @param fieldPaths Field paths to validate
 */
class RequiredValidator(
    vararg fieldPaths: String
) : AbstractFieldPathValidator(*fieldPaths) {

    override fun validate(fieldExpressions: Collection<FieldExpression>, errors: SearchyErrors) {
        if (fieldExpressions.isEmpty() && fieldPaths.isNotEmpty()) {
            errors.reject("required", "Missing required fields: {0}.", fieldPaths)
        } else {
            fieldPaths.forEach { fieldPath ->
                for (fieldExpression in fieldExpressions) {
                    if (fieldPath == fieldExpression.fieldInfo.fieldPath) {
                        return@forEach
                    }
                }
                errors.reject("required", "Missing required field: {0}.", fieldPath)
            }
        }
    }

}