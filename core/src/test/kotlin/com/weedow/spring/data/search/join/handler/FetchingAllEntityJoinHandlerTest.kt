package com.weedow.spring.data.search.join.handler

import com.nhaarman.mockitokotlin2.mock
import com.querydsl.core.JoinType
import com.weedow.spring.data.search.common.model.Person
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class FetchingAllEntityJoinHandlerTest {

    @Test
    fun supports() {
        val entityJoinHandler = FetchingAllEntityJoinHandler<Person>()
        val supports = entityJoinHandler.supports(mock())
        Assertions.assertThat(supports).isTrue()
    }

    @Test
    fun handle_entity_join() {
        val entityJoinHandler = FetchingAllEntityJoinHandler<Person>()
        val joinInfo = entityJoinHandler.handle(mock())
        Assertions.assertThat(joinInfo).isNotNull()
        Assertions.assertThat(joinInfo.joinType).isEqualTo(JoinType.LEFTJOIN)
        Assertions.assertThat(joinInfo.fetched).isEqualTo(true)
    }
}