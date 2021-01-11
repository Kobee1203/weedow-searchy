package com.weedow.searchy.expression.parser

import org.antlr.v4.runtime.BaseErrorListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// Not much to test, but exercise to prevent code coverage tool from showing red for default methods
internal class SyntaxErrorListenerTest {

    @Test
    fun test_default_method_syntaxErrors() {
        val syntaxErrorListener = MySyntaxErrorListener()

        assertThat(syntaxErrorListener.syntaxErrors).isEmpty()
    }

    internal class MySyntaxErrorListener : SyntaxErrorListener, BaseErrorListener()

}