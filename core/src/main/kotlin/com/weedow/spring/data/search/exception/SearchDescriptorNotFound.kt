package com.weedow.spring.data.search.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exception thrown when a [SearchDescriptor][com.weedow.spring.data.search.descriptor.SearchDescriptor] is not found from the Search Descriptor ID.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not Found")
class SearchDescriptorNotFound(searchDescriptorId: String) : Exception("Could not found the Search Descriptor with Id $searchDescriptorId") {

}
