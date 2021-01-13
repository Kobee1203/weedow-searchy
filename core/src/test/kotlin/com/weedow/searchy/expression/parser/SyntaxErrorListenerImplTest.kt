package com.weedow.searchy.expression.parser

import com.nhaarman.mockitokotlin2.mock
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SyntaxErrorListenerImplTest {

    @Test
    fun noSyntaxErrors() {
        val syntaxErrorListener = SyntaxErrorListenerImpl()

        assertThat(syntaxErrorListener.toString()).isEqualTo("No errors")
    }

    @Test
    fun syntaxErrors() {
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

        val syntaxErrorListener = SyntaxErrorListenerImpl()
        syntaxErrorListener.syntaxError(recognizer1, offendingSymbol1, line1, charPositionInLine1, msg1, e1)
        syntaxErrorListener.syntaxError(recognizer2, offendingSymbol2, line2, charPositionInLine2, msg2, e2)

        assertThat(syntaxErrorListener.syntaxErrors)
            .containsExactly(
                SyntaxError(recognizer1, offendingSymbol1, line1, charPositionInLine1, msg1, e1),
                SyntaxError(recognizer2, offendingSymbol2, line2, charPositionInLine2, msg2, e2)
            )

        assertThat(syntaxErrorListener.toString())
            .isEqualTo(
                "Syntax Errors: [\n" +
                        "SyntaxError(recognizer=recognizer1, offendingSymbol=$offendingSymbol1, line=${line1}, charPositionInLine=${charPositionInLine1}, message=${msg1}, exception=$e1),\n" +
                        "SyntaxError(recognizer=recognizer2, offendingSymbol=$offendingSymbol2, line=${line2}, charPositionInLine=${charPositionInLine2}, message=${msg2}, exception=$e2)" +
                        "\n]"
            )
    }

}