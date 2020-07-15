package com.weedow.spring.data.search.expression.parser

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ExpressionParserException(
        val errors: Collection<SyntaxError>,
        status: HttpStatus = HttpStatus.BAD_REQUEST,
        reason: String = format(errors)
) : ResponseStatusException(status, reason) {

    companion object {
        private fun format(errors: Collection<SyntaxError>): String {
            return errors.joinToString(separator = ",\n", prefix = "Syntax Errors: [\n", postfix = "\n]") {
                "line ${it.line}:${it.charPositionInLine} ${it.message} - ${it.offendingSymbol}"
            }
        }
    }

}