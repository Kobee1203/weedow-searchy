package com.weedow.searchy.mongodb.event

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.weedow.searchy.mongodb.domain.DbSequenceGeneratorService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.core.Ordered

@ExtendWith(MockitoExtension::class)
internal class AbstractLongIdPrePersistEntityHandlerTest {

    @Test
    fun handle() {
        val sequence = 5L
        val dbSequenceGeneratorService = mock<DbSequenceGeneratorService> {
            on { this.getNextSequence("myentity_sequence") }.thenReturn(sequence)
        }

        val prePersistEntityHandler = TestLongIdPrePersistEntityHandler(dbSequenceGeneratorService)
        val entity = MyEntity()

        assertThat(prePersistEntityHandler.supports(entity)).isTrue
        assertThat(prePersistEntityHandler.supports(OtherEntity())).isFalse

        prePersistEntityHandler.handle(entity)

        assertThat(entity.counter).isEqualTo(sequence)
    }

    @Test
    fun getOrder() {
        val dbSequenceGeneratorService = mock<DbSequenceGeneratorService>()

        val prePersistEntityHandler = TestLongIdPrePersistEntityHandler(dbSequenceGeneratorService)
        assertThat(prePersistEntityHandler.order).isEqualTo(Ordered.LOWEST_PRECEDENCE)

        verifyZeroInteractions(dbSequenceGeneratorService)
    }

    internal class TestLongIdPrePersistEntityHandler(
        dbSequenceGeneratorService: DbSequenceGeneratorService
    ) : AbstractLongIdPrePersistEntityHandler(dbSequenceGeneratorService) {
        override fun supports(entity: Any): Boolean = entity is MyEntity

        override fun doHandle(entity: Any, generatedSequence: Long) {
            (entity as MyEntity).counter = generatedSequence
        }
    }

    internal class MyEntity {
        var counter: Long = 0
    }

    internal class OtherEntity
}