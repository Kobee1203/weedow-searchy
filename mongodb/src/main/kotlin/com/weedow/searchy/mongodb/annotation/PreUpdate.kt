package com.weedow.searchy.mongodb.annotation

/**
 * Specifies a callback method for the corresponding lifecycle event. This annotation may be applied to methods of an entity class.
 *
 * This makes it possible to have a behavior similar to the JPA annotation `javax.persistence.PreUpdate`.
 */
@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
annotation class PreUpdate
