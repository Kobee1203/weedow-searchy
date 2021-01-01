package com.weedow.spring.data.search.expression.parser

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Exception thrown when an error occurs while parsing a query.
 *
 * @param errors Collection of [SyntaxError]s
 * @param status HTTP Status used for the response. Default is [HttpStatus.BAD_REQUEST]
 * @param reason the associated reason. Default is the formatting of [SyntaxError]s
 */
class ExpressionParserException(
    val errors: Collection<SyntaxError>,
    status: HttpStatus = HttpStatus.BAD_REQUEST,
    reason: String = format(errors)
) : ResponseStatusException(status, reason) {

    companion object {
        private fun format(errors: Collection<SyntaxError>): String {
            return errors.joinToString(separator = ",\n", prefix = "Syntax Errors: [\n", postfix = "\n]") {
                "${it.recognizer.grammarFileName}: ${it.exception.javaClass.name} - line ${it.line}:${it.charPositionInLine} ${it.message} - ${it.offendingSymbol}"
            }
        }
    }

}