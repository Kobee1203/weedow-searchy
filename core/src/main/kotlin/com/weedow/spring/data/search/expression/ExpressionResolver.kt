package com.weedow.spring.data.search.expression

/**
 * Interface to resolve the [Expression] from the given parameters.
 */
interface ExpressionResolver {

    /**
     * Resolves the given parameters to an [Expression].
     *
     * @param rootClass Root entity class from which to look for fields
     * @param fieldPath Path of a field. The nested field path contains dots to separate the parents fields (eg. vehicle.brand)
     * @param fieldValues Value list associated with the field. May contain one or more values
     * @param operator [Operator] used to compare the [fieldValues] with the field value
     * @param negated Whether the Expression is negated.
     * @return Resolved [Expression]
     */
    fun resolveExpression(rootClass: Class<*>, fieldPath: String, fieldValues: List<String>, operator: Operator, negated: Boolean): Expression

}