package com.weedow.searchy.validation

import com.weedow.searchy.expression.FieldExpression

/**
 * Interface to specify a [FieldExpression] Validator.
 *
 * Add the SearchyValidator implementation to a [SearchyDescriptor][com.weedow.searchy.descriptor.SearchyDescriptor]:
 * ```
 * fun personSearchyDescriptor(): SearchyDescriptor<Person> = SearchyDescriptorBuilder.builder<Person>()
 *          .validators(MyValidator())
 *          .build()
 * ```
 */
interface SearchyValidator {

    /**
     * Checks if the given [FieldExpression] is supported by the validator.
     *
     * @param fieldExpression [FieldExpression] to check
     * @return `true` if the validator supports the [FieldExpression], `false` instead.
     */
    @JvmDefault
    fun supports(fieldExpression: FieldExpression): Boolean = true

    /**
     * Override this method to validate the given single [value] related to the [FieldExpression].
     *
     * The given [SearchyErrors] instance can be used to report any resulting validation errors.
     *
     * @param value single value to be validated. Cannot be a Collection ([validateCollection] is called).
     * @param fieldExpression [FieldExpression] related to the [value]
     * @param errors [SearchyErrors] instance used to report any resulting validation errors
     */
    @JvmDefault
    fun validateSingle(value: Any, fieldExpression: FieldExpression, errors: SearchyErrors) {
        // Override this method to validate the given single [value] related to the [FieldExpression]
        // Use [SearchyErrors] instance to report any resulting validation errors
    }

    /**
     * Override this method to validate the given collection of [values] related to the [FieldExpression].
     *
     * The default behavior is to iterate over values and call [validateSingle] for each value.
     *
     * The given [SearchyErrors] instance can be used to report any resulting validation errors.
     *
     * @param values collection of values to be validated.
     * @param fieldExpression [FieldExpression] related to the [values]
     * @param errors [SearchyErrors] instance used to report any resulting validation errors
     */
    @JvmDefault
    fun validateCollection(values: Collection<Any>, fieldExpression: FieldExpression, errors: SearchyErrors) {
        values.forEach { validateSingle(it, fieldExpression, errors) }
    }

    /**
     * Validates the given [FieldExpressions][fieldExpressions].
     *
     * The default behavior:
     * * Iterate over [FieldExpressions][fieldExpressions],
     * * Filter the [FieldExpressions][fieldExpressions] according to the [supports] method
     * * Calls [validateCollection] if the [FieldExpression.value] is an instance of Collection, otherwise calls [validateSingle].
     *
     * This method can be overridden to change the default behavior.
     *
     * @see com.weedow.searchy.validation.validator.NotEmptyValidator
     */
    @JvmDefault
    fun validate(fieldExpressions: Collection<FieldExpression>, errors: SearchyErrors) {
        fieldExpressions
            .filter { fieldExpression: FieldExpression -> supports(fieldExpression) }
            .forEach { fieldExpression: FieldExpression ->
                val value = fieldExpression.value
                if (value is Collection<*>) {
                    @Suppress("UNCHECKED_CAST")
                    validateCollection(value as Collection<Any>, fieldExpression, errors)
                } else {
                    validateSingle(value, fieldExpression, errors)
                }
            }
    }

}