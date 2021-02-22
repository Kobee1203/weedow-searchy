package com.weedow.searchy.mongodb.domain

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

@ExtendWith(MockitoExtension::class)
internal class DbSequenceGeneratorServiceImplTest {

    @Mock
    private lateinit var mongoOperations: MongoOperations

    @InjectMocks
    private lateinit var dbSequenceGeneratorService: DbSequenceGeneratorServiceImpl

    @Test
    fun getNextSequence() {
        val seqName = "my_sequence_name"
        val sequence = 5L

        whenever(
            mongoOperations.findAndModify(
                eq(Query.query(Criteria.where("_id").`is`(seqName))),
                eq(Update().inc(DbSequence.SEQUENCE_FIELD_NAME, 1)),
                any(),
                eq(DbSequence::class.java)
            )
        ).thenReturn(DbSequence(seqName, sequence))

        val nextSequence = dbSequenceGeneratorService.getNextSequence(seqName)

        assertThat(nextSequence).isEqualTo(sequence)
    }

    @Test
    fun getNextSequence_when_mongoOperations_returns_null() {
        val seqName = "my_sequence_name"

        whenever(
            mongoOperations.findAndModify(
                eq(Query.query(Criteria.where("_id").`is`(seqName))),
                eq(Update().inc(DbSequence.SEQUENCE_FIELD_NAME, 1)),
                any(),
                eq(DbSequence::class.java)
            )
        ).thenReturn(null)

        val nextSequence = dbSequenceGeneratorService.getNextSequence(seqName)

        assertThat(nextSequence).isEqualTo(1L)
    }

}