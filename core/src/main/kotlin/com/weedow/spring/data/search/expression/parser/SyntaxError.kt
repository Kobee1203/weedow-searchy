package com.weedow.spring.data.search.expression.parser

import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

data class SyntaxError(
        val recognizer: Recognizer<*, *>,
        var offendingSymbol: Any,
        val line: Int,
        val charPositionInLine: Int,
        val message: String,
        val exception: RecognitionException)