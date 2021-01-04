package com.weedow.spring.data.search.exception

import com.weedow.spring.data.search.expression.Operator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exception thrown when the given [Operator] is not yet supported by Spring Data Search.
 *
 * @param operator Operator not supported
 */
@ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED, reason = "Not Implemented Operator")
class UnsupportedOperatorException(operator: String) : RuntimeException("Operator $operator is not supported")