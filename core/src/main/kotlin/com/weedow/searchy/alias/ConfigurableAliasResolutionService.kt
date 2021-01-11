package com.weedow.searchy.alias

/**
 * Configuration interface to be implemented by most if not all [AliasResolutionService] types.
 *
 * Consolidates the read-only operations exposed by [AliasResolutionService] and the mutating operations of [AliasResolverRegistry] to allow for
 * convenient ad-hoc addition and removal of [AliasResolver] through.
 */
interface ConfigurableAliasResolutionService : AliasResolutionService, AliasResolverRegistry