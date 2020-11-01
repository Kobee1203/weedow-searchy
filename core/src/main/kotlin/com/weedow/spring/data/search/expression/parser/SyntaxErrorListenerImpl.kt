package com.weedow.spring.data.search.expression.parser

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

/**
 * Default [SyntaxErrorListener] implementation.
 */
class SyntaxErrorListenerImpl : SyntaxErrorListener, BaseErrorListener() {

    override val syntaxErrors: MutableList<SyntaxError> = mutableListOf()

    override fun syntaxError(recognizer: Recognizer<*, *>, offendingSymbol: Any, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException) {
        syntaxErrors.add(SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e))
    }

    /** @suppress */
    override fun toString(): String {
        return if (syntaxErrors.isNotEmpty()) {
            syntaxErrors.joinToString(separator = ",\n", prefix = "Syntax Errors: [\n", postfix = "\n]")
        } else {
            "No errors"
        }
    }

}