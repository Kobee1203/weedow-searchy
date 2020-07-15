package com.weedow.spring.data.search.expression.parser

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.expression.Expression
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class ExpressionParserImplTest {

    @Mock
    private lateinit var expressionParserVisitorFactory: ExpressionParserVisitorFactory

    @InjectMocks
    private lateinit var expressionParser: ExpressionParserImpl

    @Test
    fun parse_successfully_when_query_is_valid() {
        val query = "firstName='John'"
        val rootClass = Person::class.java

        val mockExpression = mock<Expression>()
        val expressionParserVisitor = mock<ExpressionParserVisitor> {
            on { this.visit(any<QueryParser.StartContext>()) }.thenReturn(mockExpression)
        }
        whenever(expressionParserVisitorFactory.getExpressionParserVisitor(rootClass))
                .thenReturn(expressionParserVisitor)

        val expression = expressionParser.parse(query, rootClass)

        assertThat(expression).isEqualTo(mockExpression)
    }

    @Test
    fun fail_to_parse_when_query_is_invalid() {
        val query = "AAAAA BBBBB CCCCC"
        val rootClass = Person::class.java

        assertThatThrownBy { expressionParser.parse(query, rootClass) }
                .isInstanceOf(ExpressionParserException::class.java)
                .hasMessage("400 BAD_REQUEST \"Syntax Errors: [\n" +
                        "line 1:6 no viable alternative at input 'AAAAABBBBB' - [@1,6:10='BBBBB',<25>,1:6]\n" +
                        "]\"")

        verifyZeroInteractions(expressionParserVisitorFactory)
    }

    @Test
    fun fail_to_parse_when_query_is_empty() {
        val query = ""
        val rootClass = Person::class.java

        assertThatThrownBy { expressionParser.parse(query, rootClass) }
                .isInstanceOf(ExpressionParserException::class.java)
                .hasMessage("400 BAD_REQUEST \"Syntax Errors: [\n" +
                        "line 1:0 mismatched input '<EOF>' expecting {'(', K_NOT, FIELD_PATH_PART} - [@0,0:-1='<EOF>',<-1>,1:0]\n" +
                        "]\"")

        verifyZeroInteractions(expressionParserVisitorFactory)
    }
}