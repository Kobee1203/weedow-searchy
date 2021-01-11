package com.weedow.searchy.expression.parser

import com.nhaarman.mockitokotlin2.mock
import com.weedow.searchy.common.model.Person
import com.weedow.searchy.expression.ExpressionResolver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ExpressionParserVisitorFactoryImplTest {

    @Test
    fun getExpressionParserVisitor() {
        val rootClass = Person::class.java
        val expressionResolver = mock<ExpressionResolver>()

        val expressionParserVisitor = ExpressionParserVisitorFactoryImpl(expressionResolver).getExpressionParserVisitor(rootClass)
        assertThat(expressionParserVisitor)
            .isInstanceOf(ExpressionParserVisitorImpl::class.java)
            .extracting("expressionResolver", "rootClass")
            .containsExactly(expressionResolver, rootClass)
    }
}