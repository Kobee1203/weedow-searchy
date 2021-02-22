package com.weedow.searchy.mongodb.event

import com.nhaarman.mockitokotlin2.whenever
import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.Ordered

@ExtendWith(MockitoExtension::class)
internal class LongIdPrePersistEntityHandlerTest {

    @Mock
    private lateinit var dbSequenceGeneratorService: DbSequenceGeneratorService

    @InjectMocks
    private lateinit var prePersistEntityHandler: LongIdPrePersistEntityHandler

    @Test
    fun supports() {
        val entity = SupportedEntity()

        val supports = prePersistEntityHandler.supports(entity)

        assertThat(supports).isTrue
    }

    @Test
    fun doesnt_support_id_field_without_id_annotation() {
        val entity = EntityWithoutIdField()

        val supports = prePersistEntityHandler.supports(entity)

        assertThat(supports).isFalse
    }

    @Test
    fun doesnt_support_bad_id_field_type() {
        val entity = EntityWithNotLongId()

        val supports = prePersistEntityHandler.supports(entity)

        assertThat(supports).isFalse
    }

    @Test
    fun doesnt_support_not_null_id_field() {
        val entity = SupportedEntity()
        entity.id = 1L

        val supports = prePersistEntityHandler.supports(entity)

        assertThat(supports).isFalse
    }

    @Test
    fun doHandle() {
        val entity = SupportedEntity()

        whenever(dbSequenceGeneratorService.getNextSequence("supportedentity_sequence")).thenReturn(5L)

        prePersistEntityHandler.handle(entity)

        assertThat(entity.id).isEqualTo(5)
    }

    @Test
    fun getOrder() {
        assertThat(prePersistEntityHandler.order).isEqualTo(Ordered.LOWEST_PRECEDENCE - 20)
    }

    internal class SupportedEntity {
        var id: Long? = null
    }

    internal class EntityWithoutIdField {
        var _id: Long? = null
    }

    internal class EntityWithNotLongId {
        var id: String? = null
    }

}