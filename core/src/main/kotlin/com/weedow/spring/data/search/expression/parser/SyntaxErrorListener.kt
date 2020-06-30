package com.weedow.spring.data.search.expression.parser

import org.antlr.v4.runtime.ANTLRErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

interface SyntaxErrorListener : ANTLRErrorListener {

    val syntaxErrors: List<SyntaxError>
        get() = listOf()

    override fun syntaxError(recognizer: Recognizer<*, *>, offendingSymbol: Any, line: Int, charPositionInLine: Int, msg: String, e: RecognitionException)

}