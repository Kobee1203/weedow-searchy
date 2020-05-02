package com.weedow.spring.data.search.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not Found")
class SearchDescriptorNotFound(searchDescriptorId: String) : Exception("Could not found the Search Descriptor with Id $searchDescriptorId") {

}
