package com.weedow.spring.data.search.join.handler

import com.nhaarman.mockitokotlin2.mock
import com.weedow.spring.data.search.common.model.Person
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.criteria.JoinType

@ExtendWith(MockitoExtension::class)
internal class FetchingAllEntityJoinHandlerTest {

    @Test
    fun supports() {
        val entityClass: Class<*> = Any::class.java
        val fieldClass: Class<*> = Any::class.java
        val fieldName = "addressEntities"
        val joinAnnotation = mock<Annotation>()

        val entityJoinHandler = FetchingAllEntityJoinHandler<Person>()
        val supports = entityJoinHandler.supports(entityClass, fieldClass, fieldName, joinAnnotation)
        Assertions.assertThat(supports).isTrue()
    }

    @Test
    fun handle_entity_join() {
        val entityClass: Class<*> = Any::class.java
        val fieldClass: Class<*> = Any::class.java
        val fieldName = "addressEntities"
        val joinAnnotation = mock<Annotation>()

        val entityJoinHandler = FetchingAllEntityJoinHandler<Person>()
        val joinInfo = entityJoinHandler.handle(entityClass, fieldClass, fieldName, joinAnnotation)
        Assertions.assertThat(joinInfo).isNotNull()
        Assertions.assertThat(joinInfo.joinType).isEqualTo(JoinType.LEFT)
        Assertions.assertThat(joinInfo.fetched).isEqualTo(true)
    }
}