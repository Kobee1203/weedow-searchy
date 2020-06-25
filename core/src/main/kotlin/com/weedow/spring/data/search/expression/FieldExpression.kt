package com.weedow.spring.data.search.expression

/**
 * Interface to represent an expression related to a Field.
 */
interface FieldExpression {

    /**
     * Returns field information.
     */
    val fieldInfo: FieldInfo

    /**
     * Returns value to compare with the field value
     */
    val value: Any

    /**
     * Returns the [Operator] used to compare the [value] with the field value
     */
    val operator: Operator

    /**
     * Whether the expression is negated.
     */
    val negated: Boolean

}