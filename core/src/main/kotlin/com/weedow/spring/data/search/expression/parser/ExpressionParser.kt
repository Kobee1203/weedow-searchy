package com.weedow.spring.data.search.expression.parser

import com.weedow.spring.data.search.expression.Expression

interface ExpressionParser {

    fun parse(query: String, rootClass: Class<*>): Expression

}