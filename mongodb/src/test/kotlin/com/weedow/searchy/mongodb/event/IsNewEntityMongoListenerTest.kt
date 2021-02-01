package com.weedow.searchy.mongodb.event

import com.nhaarman.mockitokotlin2.*
import com.weedow.searchy.mongodb.annotation.PrePersist
import com.weedow.searchy.mongodb.annotation.PreUpdate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.mapping.PersistentEntity
import org.springframework.data.mapping.PersistentProperty
import org.springframework.data.mapping.context.PersistentEntities
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent

internal class IsNewEntityMongoListenerTest {

    @Test
    fun on_before_save_without_handlers() {
        val source = EntityWithAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(true)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(), listOf())

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        assertThat(source.counter).isEqualTo(5)
    }

    @Test
    fun on_before_save_with_handlers() {
        val source = EntityWithAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(true)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }
        val prePersistEntityHandler = mock<PrePersistEntityHandler> {
            on { this.supports(source) }.thenReturn(true)
        }
        val preUpdateEntityHandler = mock<PreUpdateEntityHandler>()

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(prePersistEntityHandler), listOf(preUpdateEntityHandler))

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        verifyZeroInteractions(preUpdateEntityHandler)
        verify(prePersistEntityHandler).handle(source)
        assertThat(source.counter).isEqualTo(5)
    }

    @Test
    fun on_before_save_with_handlers_not_supported() {
        val source = EntityWithAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(true)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }
        val prePersistEntityHandler = mock<PrePersistEntityHandler> {
            on { this.supports(source) }.thenReturn(false)
        }
        val preUpdateEntityHandler = mock<PreUpdateEntityHandler>()

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(prePersistEntityHandler), listOf(preUpdateEntityHandler))

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        verifyZeroInteractions(preUpdateEntityHandler)
        verify(prePersistEntityHandler, never()).handle(any())
        assertThat(source.counter).isEqualTo(5)
    }

    @Test
    fun on_before_save_without_annotations_and_handlers() {
        val source = EntityWithoutAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(true)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(), listOf())

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        assertThat(source.counter).isEqualTo(0)
    }

    @Test
    fun on_before_update_without_handlers() {
        val source = EntityWithAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(false)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(), listOf())

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        assertThat(source.counter).isEqualTo(10)
    }

    @Test
    fun on_before_update_with_handlers() {
        val source = EntityWithAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(false)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }
        val prePersistEntityHandler = mock<PrePersistEntityHandler>()
        val preUpdateEntityHandler = mock<PreUpdateEntityHandler> {
            on { this.supports(source) }.thenReturn(true)
        }

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(prePersistEntityHandler), listOf(preUpdateEntityHandler))

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        verifyZeroInteractions(prePersistEntityHandler)
        verify(preUpdateEntityHandler).handle(source)
        assertThat(source.counter).isEqualTo(10)
    }

    @Test
    fun on_update_save_with_handlers_not_supported() {
        val source = EntityWithAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(false)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }
        val prePersistEntityHandler = mock<PrePersistEntityHandler>()
        val preUpdateEntityHandler = mock<PreUpdateEntityHandler>  {
            on { this.supports(source) }.thenReturn(false)
        }

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(prePersistEntityHandler), listOf(preUpdateEntityHandler))

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        verifyZeroInteractions(prePersistEntityHandler)
        verify(preUpdateEntityHandler, never()).handle(any())
        assertThat(source.counter).isEqualTo(10)
    }

    @Test
    fun on_before_update_without_annotations_and_handlers() {
        val source = EntityWithoutAnnotations()

        val persistentEntity = mock<PersistentEntity<*, out PersistentProperty<*>>> {
            on { this.isNew(source) }.thenReturn(false)
        }
        val persistentEntities = mock<PersistentEntities> {
            on { this.getRequiredPersistentEntity(source.javaClass) }.thenReturn(persistentEntity)
        }

        val isNewEntityMongoListener = IsNewEntityMongoListener(persistentEntities, listOf(), listOf())

        val event = BeforeConvertEvent<Any>(source, "collection_name")
        isNewEntityMongoListener.onBeforeConvert(event)

        assertThat(source.counter).isEqualTo(0)
    }

    internal class EntityWithAnnotations {

        var counter: Int = 0

        @PrePersist
        fun beforeSave() {
            counter += 5
        }

        @PreUpdate
        fun beforeUpdate() {
            counter += 10
        }
    }

    internal class EntityWithoutAnnotations {
        var counter: Int = 0
    }
}