package com.weedow.spring.data.search.descriptor

/**
 * Configuration interface to be implemented by most if not all [SearchDescriptorService] types.
 *
 * Consolidates the read-only operations exposed by [SearchDescriptorService] and the mutating operations of [SearchDescriptorRegistry] to allow for
 * convenient ad-hoc addition and removal of [SearchDescriptor] through.
 */
interface ConfigurableSearchDescriptorService : SearchDescriptorService, SearchDescriptorRegistry