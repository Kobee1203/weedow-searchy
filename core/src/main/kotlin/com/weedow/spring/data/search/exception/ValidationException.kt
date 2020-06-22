package com.weedow.spring.data.search.exception

import com.weedow.spring.data.search.validation.DataSearchError
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ValidationException(
        val errors: List<DataSearchError>,
        status: HttpStatus = HttpStatus.BAD_REQUEST,
        reason: String = format(errors)
) : ResponseStatusException(status, reason) {

    companion object {
        private fun format(errors: List<DataSearchError>): String {
            return errors.joinToString(
                    separator = ", ",
                    prefix = "Validation Errors: [",
                    postfix = "]"
            ) { error -> "${error.errorCode}: ${error.errorMessage}" }
        }
    }
}