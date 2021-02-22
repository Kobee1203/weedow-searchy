package com.weedow.searchy.mongodb.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

// Not much to test, but exercise to prevent code coverage tool from showing red
internal class DbSequenceTest {

    @Test
    fun test_db_sequence() {
        val dbSequence = DbSequence("123456", 1)

        assertThat(dbSequence.id).isEqualTo("123456")
        assertThat(dbSequence.sequence).isEqualTo(1)

        assertThat(DbSequence.SEQUENCE_FIELD_NAME).isEqualTo("value")
    }
}