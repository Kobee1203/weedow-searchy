package com.weedow.searchy.mongodb.event

import com.nhaarman.mockitokotlin2.whenever
import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import com.weedow.searchy.mongodb.domain.MongoPersistable
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.Ordered
import org.springframework.data.annotation.Id

@ExtendWith(MockitoExtension::class)
internal class LongIdMongoPersistablePrePersistEntityHandlerTest {

    @Mock
    private lateinit var dbSequenceGeneratorService: DbSequenceGeneratorService

    @InjectMocks
    private lateinit var prePersistEntityHandler: LongIdMongoPersistablePrePersistEntityHandler

    @Test
    fun supports() {
        val entity = SupportedEntity()

        val supports = prePersistEntityHandler.supports(entity)

        Assertions.assertThat(supports).isTrue
    }

    @Test
    fun doesnt_support_entity_that_doesnt_extend_mongopersistable() {
        val entity = EntityWithoutMongoPersistable()

        val supports = prePersistEntityHandler.supports(entity)

        Assertions.assertThat(supports).isFalse
    }

    @Test
    fun doesnt_support_bad_id_field_type() {
        val entity = EntityWithNotLongId()

        val supports = prePersistEntityHandler.supports(entity)

        Assertions.assertThat(supports).isFalse
    }

    @Test
    fun doesnt_support_not_null_id_field() {
        val entity = SupportedEntity()
        entity.setId(1L)

        val supports = prePersistEntityHandler.supports(entity)

        Assertions.assertThat(supports).isFalse
    }

    @Test
    fun doHandle() {
        val entity = SupportedEntity()

        whenever(dbSequenceGeneratorService.getNextSequence("supportedentity_sequence")).thenReturn(5L)

        prePersistEntityHandler.handle(entity)

        Assertions.assertThat(entity.id).isEqualTo(5)
    }

    @Test
    fun getOrder() {
        Assertions.assertThat(prePersistEntityHandler.order).isEqualTo(Ordered.HIGHEST_PRECEDENCE + 20)
    }

    internal class SupportedEntity : MongoPersistable<Long>()

    internal class EntityWithoutMongoPersistable {
        @Id
        private var id: Long? = null
    }

    internal class EntityWithNotLongId : MongoPersistable<String>()
}