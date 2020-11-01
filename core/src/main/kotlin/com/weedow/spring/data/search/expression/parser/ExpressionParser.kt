package com.weedow.spring.data.search.expression.parser

import com.weedow.spring.data.search.expression.Expression

/**
 * Interface to parse the given query to an [Expression].
 */
interface ExpressionParser {

    /**
     * Parse the given query and return the resolved [Expression].
     *
     * @param query query to parse
     * @param rootClass Root entity class from which to look for fields found in the query
     * @return Resolved [Expression]
     */
    fun parse(query: String, rootClass: Class<*>): Expression

}