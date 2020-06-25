package com.weedow.spring.data.search.validation

import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.exception.ValidationException
import com.weedow.spring.data.search.expression.FieldExpression

/**
 * Service interface to validate the field expressions computed from the query parameters.
 */
interface DataSearchValidationService {

    /**
     * Validates the given [FieldExpressions][FieldExpression].
     *
     * @param fieldExpressions field expressions to validate
     * @param searchDescriptor [SearchDescriptor] of an Entity related to the fields
     *
     * @throws ValidationException when there are any validation errors
     */
    @Throws(ValidationException::class)
    fun <T> validate(fieldExpressions: Collection<FieldExpression>, searchDescriptor: SearchDescriptor<T>)
}