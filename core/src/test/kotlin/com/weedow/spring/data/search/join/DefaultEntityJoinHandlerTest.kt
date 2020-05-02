package com.weedow.spring.data.search.join

import com.weedow.spring.data.search.example.model.Address
import com.weedow.spring.data.search.example.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.persistence.criteria.JoinType

internal class DefaultEntityJoinHandlerTest {

    @Test
    fun supports() {
        val entityJoinHandler = DefaultEntityJoinHandler<Person>()
        val fieldJoinInfo = FieldJoinInfo(Person::class.java, Person::class.java, Person::class.java.getDeclaredField("addressEntities"))
        val supports = entityJoinHandler.supports(fieldJoinInfo)
        assertThat(supports).isTrue()
    }

    @Test
    fun handle_field_join() {
        val entityJoinHandler = DefaultEntityJoinHandler<Person>()
        val fieldJoinInfo = FieldJoinInfo(Person::class.java, Person::class.java, Person::class.java.getDeclaredField("addressEntities"))
        val fieldJoin = entityJoinHandler.handle(fieldJoinInfo)
        assertThat(fieldJoin).isNotNull()
        assertThat(fieldJoin.parentClass).isEqualTo(Person::class.java)
        assertThat(fieldJoin.fieldClass).isEqualTo(Address::class.java)
        assertThat(fieldJoin.fieldName).isEqualTo("addressEntities")
        assertThat(fieldJoin.joinName).isEqualTo(Address::class.java.canonicalName)
        assertThat(fieldJoin.joinType).isEqualTo(JoinType.INNER)
        assertThat(fieldJoin.fetched).isEqualTo(false)
    }

    @Test
    fun handle_field_join_for_field_with_ElementCollection_annotation() {
        val entityJoinHandler = DefaultEntityJoinHandler<Person>()
        val fieldJoinInfo = FieldJoinInfo(Person::class.java, Person::class.java, Person::class.java.getDeclaredField("nickNames"))
        val fieldJoin = entityJoinHandler.handle(fieldJoinInfo)
        assertThat(fieldJoin).isNotNull()
        assertThat(fieldJoin.parentClass).isEqualTo(Person::class.java)
        assertThat(fieldJoin.fieldClass).isEqualTo(String::class.java)
        assertThat(fieldJoin.fieldName).isEqualTo("nickNames")
        assertThat(fieldJoin.joinName).isEqualTo("nickNames")
        assertThat(fieldJoin.joinType).isEqualTo(JoinType.INNER)
        assertThat(fieldJoin.fetched).isEqualTo(false)
    }
}