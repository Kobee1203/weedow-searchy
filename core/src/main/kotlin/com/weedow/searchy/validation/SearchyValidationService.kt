package com.weedow.searchy.validation

import com.weedow.searchy.descriptor.SearchyDescriptor
import com.weedow.searchy.exception.ValidationException
import com.weedow.searchy.expression.FieldExpression

/**
 * Service interface to validate the field expressions computed from the query parameters.
 */
interface SearchyValidationService {

    /**
     * Validates the given [FieldExpressions][FieldExpression].
     *
     * @param fieldExpressions field expressions to validate
     * @param searchyDescriptor [SearchyDescriptor] of an Entity related to the fields
     *
     * @throws ValidationException when there are any validation errors
     */
    @Throws(ValidationException::class)
    fun <T> validate(fieldExpressions: Collection<FieldExpression>, searchyDescriptor: SearchyDescriptor<T>)
}