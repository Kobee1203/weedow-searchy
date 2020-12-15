package com.weedow.spring.data.search.join.handler

import com.nhaarman.mockitokotlin2.mock
import com.querydsl.core.JoinType
import com.weedow.spring.data.search.common.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class DefaultEntityJoinHandlerTest {

    @Test
    fun supports() {
        val entityJoinHandler = DefaultEntityJoinHandler()
        val supports = entityJoinHandler.supports(mock())
        assertThat(supports).isTrue()
    }

    @Test
    fun handle_entity_join() {
        val entityJoinHandler = DefaultEntityJoinHandler()
        val joinInfo = entityJoinHandler.handle(mock())
        assertThat(joinInfo).isNotNull()
        assertThat(joinInfo.joinType).isEqualTo(JoinType.LEFTJOIN)
        assertThat(joinInfo.fetched).isEqualTo(false)
    }

}