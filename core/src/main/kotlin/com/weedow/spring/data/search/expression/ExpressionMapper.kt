package com.weedow.spring.data.search.expression

/**
 * Interface to map the given parameters to [Expressions][Expression] wrapped into a [RootExpression].
 */
interface ExpressionMapper {

    /**
     * Convert the specified [parameters][params] to [Expressions][Expression].
     *
     * The [parameters map][params] contains a field path as a key and a value list associated with the field as value.
     *
     * @param params to be converted to [Expression]
     * @param rootClass Root entity class from which to look for fields
     */
    fun <T> toExpression(params: Map<String, List<String>>, rootClass: Class<T>): RootExpression<T>

}