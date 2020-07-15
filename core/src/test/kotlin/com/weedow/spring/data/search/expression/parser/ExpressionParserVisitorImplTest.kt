package com.weedow.spring.data.search.expression.parser

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.weedow.spring.data.search.common.model.Person
import com.weedow.spring.data.search.exception.UnsupportedOperatorException
import com.weedow.spring.data.search.expression.*
import com.weedow.spring.data.search.utils.NullValue
import org.antlr.v4.runtime.tree.TerminalNode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.junit.jupiter.MockitoExtension
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
internal class ExpressionParserVisitorImplTest {

    private lateinit var expressionResolver: ExpressionResolver

    private lateinit var rootClass: Class<*>

    private lateinit var expressionParserVisitor: ExpressionParserVisitorImpl

    @BeforeEach
    fun setUp() {
        expressionResolver = mock()
        rootClass = Person::class.java

        expressionParserVisitor = ExpressionParserVisitorImpl(expressionResolver, rootClass)
    }

    @Test
    fun visitStart() {
        val expression = mock<Expression>()

        val expressionContext = mock<QueryParser.ExpressionContext> {
            on { this.accept(expressionParserVisitor) }.thenReturn(expression)
        }
        val ctx = mock<QueryParser.StartContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.expression() }.thenReturn(expressionContext)
        }

        val result = expressionParserVisitor.visitStart(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
        verifyZeroInteractions(expressionResolver)
    }

    @Test
    fun visitParenthesizedExpression() {
        val expression = mock<Expression>()

        val expressionContext = mock<QueryParser.ExpressionContext> {
            on { this.accept(expressionParserVisitor) }.thenReturn(expression)
        }
        val ctx = mock<QueryParser.ParenthesizedExpressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.expression() }.thenReturn(expressionContext)
        }

        val result = expressionParserVisitor.visitParenthesizedExpression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
        verifyZeroInteractions(expressionResolver)
    }

    @Test
    fun visitNegativeExpression() {
        val expression = mock<Expression>()

        val expressionContext = mock<QueryParser.ExpressionContext> {
            on { this.accept(expressionParserVisitor) }.thenReturn(expression)
        }
        val ctx = mock<QueryParser.NegativeExpressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.expression() }.thenReturn(expressionContext)
        }

        val result = expressionParserVisitor.visitNegativeExpression(ctx)

        assertThat(result)
                .isInstanceOf(NotExpression::class.java)
                .extracting("expression")
                .isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
        verifyZeroInteractions(expressionResolver)
    }

    @Test
    fun visitAndExpression() {
        val expression1 = mock<Expression>()
        val expression2 = mock<Expression>()

        val expressionContext1 = mock<QueryParser.ExpressionContext> {
            on { this.accept(expressionParserVisitor) }.thenReturn(expression1)
        }
        val expressionContext2 = mock<QueryParser.ExpressionContext> {
            on { this.accept(expressionParserVisitor) }.thenReturn(expression2)
        }
        val ctx = mock<QueryParser.AndExpressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.expression() }.thenReturn(listOf(expressionContext1, expressionContext2))
        }

        val result = expressionParserVisitor.visitAndExpression(ctx)

        assertThat(result).isInstanceOf(LogicalExpression::class.java)
        assertThat(result).extracting("logicalOperator").isEqualTo(LogicalOperator.AND)
        assertThat(result)
                .extracting("expressions")
                .asList()
                .containsExactly(expression1, expression2)

        verifyNoMoreInteractions(ctx)
        verifyZeroInteractions(expressionResolver)
    }

    @Test
    fun visitOrExpression() {
        val expression1 = mock<Expression>()
        val expression2 = mock<Expression>()

        val expressionContext1 = mock<QueryParser.ExpressionContext> {
            on { this.accept(expressionParserVisitor) }.thenReturn(expression1)
        }
        val expressionContext2 = mock<QueryParser.ExpressionContext> {
            on { this.accept(expressionParserVisitor) }.thenReturn(expression2)
        }
        val ctx = mock<QueryParser.OrExpressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.expression() }.thenReturn(listOf(expressionContext1, expressionContext2))
        }

        val result = expressionParserVisitor.visitOrExpression(ctx)

        assertThat(result).isInstanceOf(LogicalExpression::class.java)
        assertThat(result).extracting("logicalOperator").isEqualTo(LogicalOperator.OR)
        assertThat(result)
                .extracting("expressions")
                .asList()
                .containsExactly(expression1, expression2)

        verifyNoMoreInteractions(ctx)
        verifyZeroInteractions(expressionResolver)
    }

    @ParameterizedTest
    @MethodSource("comparison_expression_parameters")
    fun visitComparison_expression_with_string_value(operatorString: String, operator: Operator) {
        val fieldPath = "birthday"
        val value = "1981-03-12T10:36:00"

        val (ctx, expression) = mock(fieldPath, value, operator, false)

        val stringValueContext = mock<QueryParser.String_valueContext> {
            on { this.text }.thenReturn(value)
        }
        whenever(ctx.string_value()).thenReturn(stringValueContext)

        val operatorRuleContext = mock<QueryParser.Comparison_operatorContext> {
            on { this.text }.thenReturn(operatorString)
        }
        whenever(ctx.comparison_operator()).thenReturn(operatorRuleContext)

        val result = expressionParserVisitor.visitComparison_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @ParameterizedTest
    @MethodSource("comparison_expression_parameters")
    fun visitComparison_expression_with_number_value(operatorString: String, operator: Operator) {
        val fieldPath = "height"
        val value = "174"

        val (ctx, expression) = mock(fieldPath, value, operator, false)

        val numberValueContext = mock<QueryParser.Number_valueContext> {
            on { this.text }.thenReturn(value)
        }
        whenever(ctx.number_value()).thenReturn(numberValueContext)
        whenever(ctx.string_value()).thenReturn(null)

        val operatorRuleContext = mock<QueryParser.Comparison_operatorContext> {
            on { this.text }.thenReturn(operatorString)
        }
        whenever(ctx.comparison_operator()).thenReturn(operatorRuleContext)

        val result = expressionParserVisitor.visitComparison_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @ParameterizedTest
    @MethodSource("comparison_expression_parameters")
    fun visitComparison_expression_with_boolean_value(operatorString: String, operator: Operator) {
        val fieldPath = "jobEntity.active"
        val value = "true"

        val (ctx, expression) = mock(fieldPath, value, operator, false)

        val booleanValueContext = mock<QueryParser.Boolean_valueContext> {
            on { this.text }.thenReturn(value)
        }
        whenever(ctx.boolean_value()).thenReturn(booleanValueContext)
        whenever(ctx.number_value()).thenReturn(null)
        whenever(ctx.string_value()).thenReturn(null)

        val operatorRuleContext = mock<QueryParser.Boolean_comparison_operatorContext> {
            on { this.text }.thenReturn(operatorString)
        }
        whenever(ctx.boolean_comparison_operator()).thenReturn(operatorRuleContext)
        whenever(ctx.comparison_operator()).thenReturn(null)

        val result = expressionParserVisitor.visitComparison_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @Test
    fun visitComparison_expression_with_negative_operator() {
        val fieldPath = "birthday"
        val value = "1981-03-12T10:36:00"
        val operatorString = "!="
        val operator = Operator.EQUALS

        val fieldPathContext = mock<QueryParser.Field_pathContext> {
            on { this.text }.thenReturn(fieldPath)
        }
        val ctx = mock<QueryParser.Comparison_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.field_path() }.thenReturn(fieldPathContext)
        }
        val expression = mock<Expression>()
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, listOf(value), operator, true))
                .thenReturn(expression)

        val stringValueContext = mock<QueryParser.String_valueContext> {
            on { this.text }.thenReturn(value)
        }
        whenever(ctx.string_value()).thenReturn(stringValueContext)

        val operatorRuleContext = mock<QueryParser.Comparison_operatorContext> {
            on { this.text }.thenReturn(operatorString)
        }
        whenever(ctx.comparison_operator()).thenReturn(operatorRuleContext)

        val result = expressionParserVisitor.visitComparison_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @Test
    fun visitComparison_expression_with_unsupported_operator_for_comparison_operator() {
        val operatorString = "<>"

        val ctx = mock<QueryParser.Comparison_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
        }

        val operatorRuleContext = mock<QueryParser.Comparison_operatorContext> {
            on { this.text }.thenReturn(operatorString)
        }
        whenever(ctx.comparison_operator()).thenReturn(operatorRuleContext)

        assertThatThrownBy { expressionParserVisitor.visitComparison_expression(ctx) }
                .isInstanceOf(UnsupportedOperatorException::class.java)
                .hasMessage("Operator $operatorString is not supported")

        verifyNoMoreInteractions(ctx)
        verifyZeroInteractions(expressionResolver)
    }

    @Test
    fun visitComparison_expression_with_unsupported_operator_for_boolean_comparison_operator() {
        val operatorString = "<>"

        val ctx = mock<QueryParser.Comparison_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
        }

        val operatorRulecontext = mock<QueryParser.Boolean_comparison_operatorContext> {
            on { this.text }.thenReturn(operatorString)
        }
        whenever(ctx.boolean_comparison_operator()).thenReturn(operatorRulecontext)
        whenever(ctx.comparison_operator()).thenReturn(null)

        assertThatThrownBy { expressionParserVisitor.visitComparison_expression(ctx) }
                .isInstanceOf(UnsupportedOperatorException::class.java)
                .hasMessage("Operator $operatorString is not supported")

        verifyNoMoreInteractions(ctx)
        verifyZeroInteractions(expressionResolver)
    }

    @ParameterizedTest
    @MethodSource("comparison_expression_with_quotes_parameters")
    fun visitComparison_expression_with_quotes(inputValue: String, finalValue: String) {
        val fieldPath = "fieldPath"

        val (ctx, expression) = mock(fieldPath, finalValue, Operator.EQUALS, false)

        val stringValueContext = mock<QueryParser.String_valueContext> {
            on { this.text }.thenReturn(inputValue)
        }
        whenever(ctx.string_value()).thenReturn(stringValueContext)

        val operatorRuleContext = mock<QueryParser.Comparison_operatorContext> {
            on { this.text }.thenReturn("=")
        }
        whenever(ctx.comparison_operator()).thenReturn(operatorRuleContext)

        val result = expressionParserVisitor.visitComparison_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @ParameterizedTest
    @MethodSource("not_operator_parameters")
    fun visitNull_comparison_expression(notMock: TerminalNode?, negated: Boolean) {
        val fieldPath = "firstName"

        val fieldPathContext = mock<QueryParser.Field_pathContext> {
            on { this.text }.thenReturn(fieldPath)
        }
        val ctx = mock<QueryParser.Null_comparison_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.field_path() }.thenReturn(fieldPathContext)
            on { this.K_NOT() }.thenReturn(notMock)
        }

        val expression = mock<Expression>()
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, listOf(NullValue.NULL_VALUE), Operator.EQUALS, negated))
                .thenReturn(expression)

        val result = expressionParserVisitor.visitNull_comparison_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @ParameterizedTest
    @MethodSource("not_operator_parameters")
    fun visitIn_expression(notMock: TerminalNode?, negated: Boolean) {
        val fieldPath = "fieldPath"
        val value1 = "John"
        val value2 = "174.3"
        val value3 = "true"

        // Test the different values (string, number, boolean)
        val valueContext1 = mock<QueryParser.String_valueContext> {
            on { this.text }.thenReturn(value1)
        }
        val valueContext2 = mock<QueryParser.Number_valueContext> {
            on { this.text }.thenReturn(value2)
        }
        val valueContext3 = mock<QueryParser.Boolean_valueContext> {
            on { this.text }.thenReturn(value3)
        }
        val values = listOf<QueryParser.ValueContext>(
                mock {
                    on {this.string_value()}.thenReturn(valueContext1)
                },
                mock {
                    on {this.string_value()}.thenReturn(null)
                    on {this.number_value()}.thenReturn(valueContext2)
                },
                mock {
                    on {this.string_value()}.thenReturn(null)
                    on {this.number_value()}.thenReturn(null)
                    on {this.boolean_value()}.thenReturn(valueContext3)
                }
        )

        val fieldPathContext = mock<QueryParser.Field_pathContext> {
            on { this.text }.thenReturn(fieldPath)
        }
        val ctx = mock<QueryParser.In_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.field_path() }.thenReturn(fieldPathContext)
            on { this.K_NOT() }.thenReturn(notMock)
            on { this.value() }.thenReturn(values)
        }

        val expression = mock<Expression>()
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, listOf(value1, value2, value3), Operator.IN, negated))
                .thenReturn(expression)

        val result = expressionParserVisitor.visitIn_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @Test
    fun visitMatches_expression() {
        val fieldPath = "firstName"
        val value = "*Jo*"

        val fieldPathContext = mock<QueryParser.Field_pathContext> {
            on { this.text }.thenReturn(fieldPath)
        }
        val ctx = mock<QueryParser.Matches_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.field_path() }.thenReturn(fieldPathContext)
        }

        val stringValueContext = mock<QueryParser.String_valueContext> {
            on { this.text }.thenReturn(value)
        }
        whenever(ctx.string_value()).thenReturn(stringValueContext)

        val expression = mock<Expression>()
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, listOf(value), Operator.MATCHES, false))
                .thenReturn(expression)

        val result = expressionParserVisitor.visitMatches_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    @Test
    fun visitImatches_expression() {
        val fieldPath = "firstName"
        val value = "*JO*"

        val fieldPathContext = mock<QueryParser.Field_pathContext> {
            on { this.text }.thenReturn(fieldPath)
        }
        val ctx = mock<QueryParser.Imatches_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.field_path() }.thenReturn(fieldPathContext)
        }

        val stringValueContext = mock<QueryParser.String_valueContext> {
            on { this.text }.thenReturn(value)
        }
        whenever(ctx.string_value()).thenReturn(stringValueContext)

        val expression = mock<Expression>()
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, listOf(value), Operator.IMATCHES, false))
                .thenReturn(expression)

        val result = expressionParserVisitor.visitImatches_expression(ctx)

        assertThat(result).isEqualTo(expression)

        verifyNoMoreInteractions(ctx)
    }

    private fun mock(fieldPath: String, value: String, operator: Operator, negated: Boolean): Pair<QueryParser.Comparison_expressionContext, Expression> {
        val fieldPathContext = mock<QueryParser.Field_pathContext> {
            on { this.text }.thenReturn(fieldPath)
        }
        val ctx = mock<QueryParser.Comparison_expressionContext> {
            on { this.text }.thenReturn("{EXPRESSION}")
            on { this.field_path() }.thenReturn(fieldPathContext)
        }

        val expression = mock<Expression>()
        whenever(expressionResolver.resolveExpression(rootClass, fieldPath, listOf(value), operator, negated))
                .thenReturn(expression)

        return Pair(ctx, expression)
    }

    companion object {
        @JvmStatic
        @Suppress("unused")
        private fun comparison_expression_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("=", Operator.EQUALS),
                    Arguments.of("<", Operator.LESS_THAN),
                    Arguments.of("<=", Operator.LESS_THAN_OR_EQUALS),
                    Arguments.of(">", Operator.GREATER_THAN),
                    Arguments.of(">=", Operator.GREATER_THAN_OR_EQUALS)
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun comparison_expression_with_quotes_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of("'my value with \\'single quote\\''", "my value with 'single quote'"),
                    Arguments.of("\"my value with \\\"single quote\\\"\"", "my value with \"single quote\"")
            )
        }

        @JvmStatic
        @Suppress("unused")
        private fun not_operator_parameters(): Stream<Arguments> {
            return Stream.of(
                    Arguments.of(null, false),
                    Arguments.of(mock<TerminalNode>(), true)
            )
        }
    }
}