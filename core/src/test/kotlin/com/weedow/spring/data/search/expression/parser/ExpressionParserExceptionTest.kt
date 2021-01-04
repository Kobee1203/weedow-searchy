package com.weedow.spring.data.search.expression.parser

import com.nhaarman.mockitokotlin2.mock
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class ExpressionParserExceptionTest {

    @Test
    fun test_ExpressionParserException() {

        val grammarFileName1 = "Query.g4"
        val recognizer1 = mock<Recognizer<*, *>>(name = "recognizer1") {
            on {this.grammarFileName}.thenReturn(grammarFileName1)
        }
        val offendingSymbol1 = "[@1,6:10='BBBBB',<19>,1:6]"
        val line1 = 1
        val charPositionInLine1 = 6
        val msg1 = "no viable alternative at input 'AAAAABBBBB'"
        val e1 = RecognitionException(recognizer1, mock(), mock())
        val exceptionName1 = RecognitionException::class.java.name

        val grammarFileName2 = "Query.g4"
        val recognizer2 = mock<Recognizer<*, *>>(name = "recognizer2") {
            on {this.grammarFileName}.thenReturn(grammarFileName2)
        }
        val offendingSymbol2 = "[@10,15:10='XXXXXX',<19>,10:15]"
        val line2 = 10
        val charPositionInLine2 = 15
        val msg2 = "no viable alternative at input 'XXXXXX'"
        val e2 = RecognitionException(recognizer1, mock(), mock())
        val exceptionName2 = RecognitionException::class.java.name

        val syntaxError1 = SyntaxError(recognizer1, offendingSymbol1, line1, charPositionInLine1, msg1, e1)
        val syntaxError2 = SyntaxError(recognizer2, offendingSymbol2, line2, charPositionInLine2, msg2, e2)

        val exception = ExpressionParserException(listOf(syntaxError1, syntaxError2))

        assertThat(exception.errors).containsExactly(syntaxError1, syntaxError2)
        assertThat(exception.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(exception.reason).isEqualTo(
            "Syntax Errors: [\n" +
                    "$grammarFileName1: $exceptionName1 - line $line1:$charPositionInLine1 $msg1 - $offendingSymbol1,\n" +
                    "$grammarFileName2: $exceptionName2 - line $line2:$charPositionInLine2 $msg2 - $offendingSymbol2" +
                    "\n]"
        )
        assertThat(exception.message).isEqualTo(
            "${HttpStatus.BAD_REQUEST} \"Syntax Errors: [\n" +
                    "$grammarFileName1: $exceptionName1 - line $line1:$charPositionInLine1 $msg1 - $offendingSymbol1,\n" +
                    "$grammarFileName2: $exceptionName2 - line $line2:$charPositionInLine2 $msg2 - $offendingSymbol2" +
                    "\n]\""
        )
    }

    @Test
    fun test_ExpressionParserException_with_custom_values() {
        val recognizer1 = mock<Recognizer<*, *>>(name = "recognizer1")
        val offendingSymbol1 = "[@1,6:10='BBBBB',<19>,1:6]"
        val line1 = 1
        val charPositionInLine1 = 6
        val msg1 = "no viable alternative at input 'AAAAABBBBB'"
        val e1 = RecognitionException(recognizer1, mock(), mock())

        val recognizer2 = mock<Recognizer<*, *>>(name = "recognizer2")
        val offendingSymbol2 = "[@10,15:10='XXXXXX',<19>,10:15]"
        val line2 = 10
        val charPositionInLine2 = 15
        val msg2 = "no viable alternative at input 'XXXXXX'"
        val e2 = RecognitionException(recognizer1, mock(), mock())

        val syntaxError1 = SyntaxError(recognizer1, offendingSymbol1, line1, charPositionInLine1, msg1, e1)
        val syntaxError2 = SyntaxError(recognizer2, offendingSymbol2, line2, charPositionInLine2, msg2, e2)

        val reason = "An error occurred"
        val exception = ExpressionParserException(listOf(syntaxError1, syntaxError2), HttpStatus.INTERNAL_SERVER_ERROR, reason)

        assertThat(exception.errors).containsExactly(syntaxError1, syntaxError2)
        assertThat(exception.status).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(exception.reason).isEqualTo(reason)
        assertThat(exception.message).isEqualTo("${HttpStatus.INTERNAL_SERVER_ERROR} \"$reason\"")
    }
}