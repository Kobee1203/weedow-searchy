package com.weedow.spring.data.search.exception

import com.weedow.spring.data.search.validation.DataSearchError
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.text.MessageFormat
import java.util.*

/**
 * Exception thrown when a validation fails.
 *
 * @param errors Collection of [DataSearchError]s
 * @param status HTTP Status used for the response. Default is [HttpStatus.BAD_REQUEST]
 * @param reason the associated reason. Default is the formatting of [DataSearchError]s
 */
class ValidationException(
        val errors: Collection<DataSearchError>,
        status: HttpStatus = HttpStatus.BAD_REQUEST,
        reason: String = format(errors)
) : ResponseStatusException(status, reason) {

    companion object {
        private fun format(errors: Collection<DataSearchError>): String {
            val locale = LocaleContextHolder.getLocale()
            return errors.joinToString(
                    separator = ", ",
                    prefix = "Validation Errors: [",
                    postfix = "]"
            ) { error -> format(error, locale) }
        }

        private fun format(error: DataSearchError, locale: Locale): String {
            val message = MessageFormat(error.errorMessage, locale).format(error.arguments)
            return "${error.errorCode}: $message"
        }
    }
}