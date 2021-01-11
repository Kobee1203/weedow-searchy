package com.weedow.searchy.descriptor

/**
 * Configuration interface to be implemented by most if not all [SearchyDescriptorService] types.
 *
 * Consolidates the read-only operations exposed by [SearchyDescriptorService] and the mutating operations of [SearchyDescriptorRegistry] to allow for
 * convenient ad-hoc addition and removal of [SearchyDescriptor] through.
 */
interface ConfigurableSearchyDescriptorService : SearchyDescriptorService, SearchyDescriptorRegistry