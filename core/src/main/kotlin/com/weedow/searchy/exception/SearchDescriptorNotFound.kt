package com.weedow.searchy.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exception thrown when a [SearchyDescriptor][com.weedow.searchy.descriptor.SearchyDescriptor] is not found from the Search Descriptor ID.
 *
 * @param searchyDescriptorId Search Descriptor ID
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Not Found")
class SearchyDescriptorNotFound(searchyDescriptorId: String) : RuntimeException("Could not found the Search Descriptor with Id $searchyDescriptorId")