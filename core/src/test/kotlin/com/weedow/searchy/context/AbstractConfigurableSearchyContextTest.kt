package com.weedow.searchy.context

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.querydsl.core.types.dsl.*
import com.weedow.searchy.query.querytype.ElementType
import com.weedow.searchy.query.querytype.QEntity
import com.weedow.searchy.query.querytype.QEntityImpl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import javax.persistence.*

@ExtendWith(MockitoExtension::class)
internal class AbstractConfigurableSearchyContextTest {

    @Test
    fun add_entity_class() {
        val searchyContext: ConfigurableSearchyContext = spy<AbstractConfigurableSearchyContext>()

        val qEntity = searchyContext.add(Person::class.java)
        assertThat(searchyContext.get(Person::class.java)).isSameAs(qEntity)

        // This call does not add two times the same entity class -> return the same QEntity
        val qEntity1 = searchyContext.add(Person::class.java)
        assertThat(qEntity1).isSameAs(qEntity)
        assertThat(searchyContext.get(Person::class.java)).isSameAs(qEntity)
    }

    @Test
    fun call_default_method_when_entity_class_not_found() {
        val searchyContext: ConfigurableSearchyContext = spy<AbstractConfigurableSearchyContext>()

        // With default default-method

        val entityClass = Person::class.java
        assertThatThrownBy { searchyContext.get(entityClass) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Could not found the QEntity for $entityClass")

        // With custom default-method

        val defaultQEntity = mock<QEntity<Person>>()
        val qEntity = searchyContext.get(Person::class.java) { defaultQEntity }
        assertThat(qEntity).isSameAs(defaultQEntity)
    }

    @Test
    fun get_all_property_infos() {
        val searchyContext: ConfigurableSearchyContext = spy<AbstractConfigurableSearchyContext> {
            on { this.entityAnnotations }.thenReturn(listOf(Entity::class.java))
        }

        val propertyInfos = searchyContext.getAllPropertyInfos(Person::class.java)

        assertThat(propertyInfos).hasSize(8)

        assertThat(propertyInfos)
            .extracting("parentClass", "fieldName", "elementType", "type", "parameterizedTypes", "annotations", "queryType", "qName")
            .containsExactly(
                tuple("id", ElementType.NUMBER, Long::class.javaObjectType, emptyList(), NumberPath::class.java),
                tuple("firstName", ElementType.STRING, String::class.java, emptyList(), StringPath::class.java),
                tuple("birthday", ElementType.DATETIME, LocalDateTime::class.java, emptyList(), DateTimePath::class.java),
                tuple("height", ElementType.NUMBER, Double::class.javaObjectType, emptyList(), NumberPath::class.java),
                tuple("nickNames", ElementType.SET, Set::class.java, listOf(String::class.java), StringPath::class.java),
                tuple(
                    "characteristics",
                    ElementType.MAP,
                    Map::class.java,
                    listOf(String::class.java, Boolean::class.javaObjectType),
                    BooleanPath::class.java
                ),
                tuple(
                    "addresses",
                    ElementType.SET,
                    Set::class.java,
                    listOf(Address::class.java),
                    QEntityImpl::class.java,
                    Address::class.java.canonicalName
                ),
                tuple("job", ElementType.ENTITY, Job::class.java, emptyList(), QEntityImpl::class.java, Job::class.java.canonicalName)
            )
    }

    @Test
    fun is_entity() {
        val searchyContext: ConfigurableSearchyContext = spy<AbstractConfigurableSearchyContext> {
            on { this.entityAnnotations }.thenReturn(listOf(Entity::class.java))
        }

        assertThat(searchyContext.isEntity(Person::class.java)).isTrue
        assertThat(searchyContext.isEntity(Address::class.java)).isTrue
        assertThat(searchyContext.isEntity(Job::class.java)).isTrue
        assertThat(searchyContext.isEntity(NotEntity::class.java)).isFalse
    }

    @Test
    fun is_join_annotations() {
        val searchyContext: ConfigurableSearchyContext = spy<AbstractConfigurableSearchyContext> {
            on { this.joinAnnotations }.thenReturn(listOf(Entity::class.java, Column::class.java))
        }

        assertThat(searchyContext.isJoinAnnotation(Entity::class.java)).isTrue
        assertThat(searchyContext.isJoinAnnotation(Column::class.java)).isTrue
        assertThat(searchyContext.isJoinAnnotation(ElementCollection::class.java)).isFalse
        assertThat(searchyContext.isJoinAnnotation(OneToMany::class.java)).isFalse
        assertThat(searchyContext.isJoinAnnotation(OneToOne::class.java)).isFalse
    }

    private fun tuple(
        fieldName: String,
        elementType: ElementType,
        type: Class<*>,
        parameterizedTypes: List<Class<*>>,
        queryType: Class<out SimpleExpression<*>>,
        qName: String = "${Person::class.java.canonicalName}.$fieldName"
    ) =
        Tuple.tuple(
            Person::class.java,
            fieldName,
            elementType,
            type,
            parameterizedTypes,
            Person::class.java.getDeclaredField(fieldName).annotations.asList(),
            queryType,
            qName
        )

    @Entity
    class Person(
        @Id
        val id: Long,

        @Column
        val firstName: String,

        @Column
        val birthday: LocalDateTime? = null,

        @Column
        val height: Double? = null,

        @ElementCollection
        var nickNames: Set<String>? = null,

        @ElementCollection
        @Column(name = "value")
        val characteristics: Map<String, Boolean>? = null,

        @OneToMany
        val addresses: Set<Address>,

        @OneToOne
        val job: Job
    ) {

        companion object {
            const val STATIC_FIELD = "STATIC_FIELD"
        }

    }

    @Entity
    class Address(
        @Id
        val id: Long,

        @Column(nullable = false)
        val street: String,

        @Column(nullable = false)
        val country: String

    )

    @Entity
    class Job(
        @Id
        val id: Long,

        @Column(nullable = false)
        val title: String,

        @Column(nullable = false)
        val company: String
    )

    class NotEntity

}