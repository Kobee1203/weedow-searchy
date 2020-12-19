package com.weedow.spring.data.search.join

import com.nhaarman.mockitokotlin2.*
import com.querydsl.core.JoinType
import com.querydsl.core.types.dsl.SimpleExpression
import com.querydsl.core.types.dsl.StringPath
import com.weedow.spring.data.search.context.DataSearchContext
import com.weedow.spring.data.search.descriptor.SearchDescriptor
import com.weedow.spring.data.search.join.handler.EntityJoinHandler
import com.weedow.spring.data.search.querydsl.querytype.ElementType
import com.weedow.spring.data.search.querydsl.querytype.PropertyInfos
import com.weedow.spring.data.search.querydsl.querytype.QEntityImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import javax.persistence.*

@ExtendWith(MockitoExtension::class)
internal class EntityJoinManagerImplTest {

    @Mock
    private lateinit var dataSearchContext: DataSearchContext

    @InjectMocks
    private lateinit var entityJoinManager: EntityJoinManagerImpl

    @Test
    fun compute_entity_without_joins() {
        val entityClass = EntityWithNoJoins::class.java

        val searchDescriptor = mock<SearchDescriptor<EntityWithNoJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(entityClass)
            on { this.entityJoinHandlers }.doReturn(emptyList())
        }

        whenever(dataSearchContext.getAllPropertyInfos(entityClass)).thenReturn(
            listOf(
                propertyInfos(
                    "${entityClass.canonicalName}.firstName",
                    entityClass,
                    "firstName",
                    ElementType.STRING,
                    emptyList(),
                    StringPath::class.java
                ),
                propertyInfos(
                    "${entityClass.canonicalName}.lastName",
                    entityClass,
                    "lastName",
                    ElementType.STRING,
                    emptyList(),
                    StringPath::class.java
                )
            )
        )

        whenever(dataSearchContext.isJoinAnnotation(Column::class.java)).thenReturn(false)

        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull
        assertThat(entityJoins.getJoins()).isEmpty()

        verify(dataSearchContext, times(2)).isJoinAnnotation(Column::class.java)
        verifyNoMoreInteractions(dataSearchContext)
    }

    @Test
    fun compute_entity_with_one_join() {
        val entityClass = EntityWithJoins::class.java

        val searchDescriptor = mock<SearchDescriptor<EntityWithJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(entityClass)
            on { this.entityJoinHandlers }.doReturn(emptyList())
        }

        whenever(dataSearchContext.getAllPropertyInfos(entityClass)).thenReturn(
            listOf(
                propertyInfos(
                    "${entityClass.canonicalName}.firstName",
                    entityClass,
                    "firstName",
                    ElementType.STRING,
                    emptyList(),
                    StringPath::class.java
                ),
                propertyInfos(
                    EntityWithJoins.OtherEntity::class.java.canonicalName,
                    entityClass,
                    "myJoin",
                    ElementType.ENTITY,
                    emptyList(),
                    QEntityImpl::class.java
                )
            )
        )

        val otherEntityClass = EntityWithJoins.OtherEntity::class.java
        whenever(dataSearchContext.isEntity(otherEntityClass)).thenReturn(true)
        whenever(dataSearchContext.getAllPropertyInfos(otherEntityClass)).thenReturn(
            listOf(
                propertyInfos("${otherEntityClass.canonicalName}.id", otherEntityClass, "id", ElementType.STRING, emptyList(), StringPath::class.java)
            )
        )

        whenever(dataSearchContext.isJoinAnnotation(Column::class.java)).thenReturn(false)
        whenever(dataSearchContext.isJoinAnnotation(OneToMany::class.java)).thenReturn(true)

        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull
        assertThat(entityJoins.getJoins()).hasSize(1)

        val joinName = otherEntityClass.canonicalName
        assertThat(entityJoins.getJoins().keys)
            .containsExactly(joinName)

        assertThat(entityJoins.getJoins().values)
            .containsExactly(
                EntityJoin("myJoin", "myJoin", joinName)
            )

        verify(dataSearchContext, times(2)).isJoinAnnotation(Column::class.java)
        verifyNoMoreInteractions(dataSearchContext)
    }

    @Test
    fun compute_entity_with_multiple_joins() {
        val entityClass = EntityWithMultipleJoins::class.java

        val searchDescriptor = mock<SearchDescriptor<EntityWithMultipleJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(entityClass)
            on { this.entityJoinHandlers }.doReturn(listOf(EntityWithMultipleJoins().MyEntityJoinHandler()))
        }

        whenever(dataSearchContext.getAllPropertyInfos(entityClass)).thenReturn(
            listOf(
                propertyInfos(
                    "${entityClass.canonicalName}.firstName",
                    entityClass,
                    "firstName",
                    ElementType.STRING,
                    emptyList(),
                    StringPath::class.java
                ),
                propertyInfos(
                    EntityWithMultipleJoins.OtherEntity::class.java.canonicalName,
                    entityClass,
                    "myJoin1",
                    ElementType.ENTITY,
                    emptyList(),
                    QEntityImpl::class.java
                )
            )
        )

        val otherEntityClass = EntityWithMultipleJoins.OtherEntity::class.java
        whenever(dataSearchContext.isEntity(otherEntityClass)).thenReturn(true)
        whenever(dataSearchContext.getAllPropertyInfos(otherEntityClass)).thenReturn(
            listOf(
                propertyInfos("${entityClass.canonicalName}.id", otherEntityClass, "id", ElementType.STRING, emptyList(), StringPath::class.java),
                propertyInfos(
                    "${otherEntityClass.canonicalName}.myJoin2",
                    otherEntityClass,
                    "myJoin2",
                    ElementType.SET,
                    listOf(String::class.java),
                    StringPath::class.java
                )
            )
        )
        whenever(dataSearchContext.isEntity(String::class.java)).thenReturn(false) // myJoin2

        whenever(dataSearchContext.isJoinAnnotation(Column::class.java)).thenReturn(false)
        whenever(dataSearchContext.isJoinAnnotation(OneToMany::class.java)).thenReturn(true)
        whenever(dataSearchContext.isJoinAnnotation(ElementCollection::class.java)).thenReturn(true)

        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull
        assertThat(entityJoins.getJoins()).hasSize(2)

        val joinName1 = otherEntityClass.canonicalName
        val joinName2 = otherEntityClass.canonicalName + "." + "myJoin2"

        assertThat(entityJoins.getJoins().keys)
            .containsExactly(joinName1, joinName2)

        assertThat(entityJoins.getJoins().values)
            .containsExactlyInAnyOrder(
                EntityJoin("myJoin1", "myJoin1", joinName1),
                EntityJoin("myJoin1.myJoin2", "myJoin2", joinName2, JoinType.LEFTJOIN, true)
            )

        verify(dataSearchContext, times(2)).isJoinAnnotation(Column::class.java)
        verifyNoMoreInteractions(dataSearchContext)
    }

    @Test
    fun compute_entity_by_skipping_entity_already_processed_or_root_entity() {
        val entityClass = EntityWithBidirectionalJoins::class.java
        val searchDescriptor = mock<SearchDescriptor<EntityWithBidirectionalJoins>> {
            on { this.id }.doReturn("entity")
            on { this.entityClass }.doReturn(entityClass)
            on { this.entityJoinHandlers }.doReturn(emptyList())
        }

        whenever(dataSearchContext.getAllPropertyInfos(entityClass)).thenReturn(
            listOf(
                propertyInfos(
                    "${entityClass.canonicalName}.firstName",
                    entityClass,
                    "firstName",
                    ElementType.STRING,
                    emptyList(),
                    StringPath::class.java
                ),
                propertyInfos(
                    EntityWithBidirectionalJoins.MyEntity1::class.java.canonicalName,
                    entityClass,
                    "myJoin1",
                    ElementType.ENTITY,
                    emptyList(),
                    QEntityImpl::class.java
                ),
                propertyInfos(
                    EntityWithBidirectionalJoins.MyEntity2::class.java.canonicalName,
                    entityClass,
                    "myJoin2",
                    ElementType.ENTITY,
                    emptyList(),
                    QEntityImpl::class.java
                )
            )
        )

        val myEntity1Class = EntityWithBidirectionalJoins.MyEntity1::class.java
        whenever(dataSearchContext.isEntity(myEntity1Class)).thenReturn(true)
        whenever(dataSearchContext.getAllPropertyInfos(myEntity1Class)).thenReturn(
            listOf(
                propertyInfos("${myEntity1Class.canonicalName}.id", myEntity1Class, "id", ElementType.STRING, emptyList(), StringPath::class.java),
                propertyInfos(
                    EntityWithBidirectionalJoins::class.java.canonicalName,
                    myEntity1Class,
                    "parent",
                    ElementType.ENTITY,
                    emptyList(),
                    QEntityImpl::class.java
                )
            )
        )

        val myEntity2Class = EntityWithBidirectionalJoins.MyEntity2::class.java
        whenever(dataSearchContext.isEntity(myEntity2Class)).thenReturn(true)
        whenever(dataSearchContext.getAllPropertyInfos(myEntity2Class)).thenReturn(
            listOf(
                propertyInfos("${myEntity2Class.canonicalName}.id", myEntity2Class, "id", ElementType.STRING, emptyList(), StringPath::class.java),
                propertyInfos(myEntity1Class.canonicalName, myEntity2Class, "myEntity1", ElementType.ENTITY, emptyList(), QEntityImpl::class.java)
            )
        )

        whenever(dataSearchContext.isJoinAnnotation(Column::class.java)).thenReturn(false)
        whenever(dataSearchContext.isJoinAnnotation(OneToMany::class.java)).thenReturn(true)
        whenever(dataSearchContext.isJoinAnnotation(ManyToOne::class.java)).thenReturn(true)
        whenever(dataSearchContext.isJoinAnnotation(OneToOne::class.java)).thenReturn(true)

        val entityJoins = entityJoinManager.computeEntityJoins(searchDescriptor)

        assertThat(entityJoins).isNotNull
        assertThat(entityJoins.getJoins()).hasSize(2)

        val joinName1 = EntityWithBidirectionalJoins.MyEntity1::class.java.canonicalName
        val joinName2 = EntityWithBidirectionalJoins.MyEntity2::class.java.canonicalName

        assertThat(entityJoins.getJoins().keys)
            .containsExactly(joinName1, joinName2)

        assertThat(entityJoins.getJoins().values)
            .containsExactlyInAnyOrder(
                EntityJoin("myJoin1", "myJoin1", joinName1),
                EntityJoin("myJoin2", "myJoin2", joinName2)
            )

        verify(dataSearchContext, times(3)).isJoinAnnotation(Column::class.java)
        verify(dataSearchContext, times(2)).isJoinAnnotation(OneToMany::class.java)
        verifyNoMoreInteractions(dataSearchContext)
    }

    private fun propertyInfos(
        qName: String,
        parentClass: Class<*>,
        fieldName: String,
        elementType: ElementType,
        parameterizedTypes: List<Class<*>>,
        queryType: Class<out SimpleExpression<*>>
    ): PropertyInfos {
        val field = parentClass.getDeclaredField(fieldName)
        return PropertyInfos(
            qName,
            parentClass,
            fieldName,
            elementType,
            field.type,
            parameterizedTypes,
            field.annotations.toList(),
            queryType
        )
    }

    internal data class EntityWithNoJoins(
        @Column(nullable = false)
        val firstName: String = "",

        @Column(nullable = false)
        val lastName: String = "",
    )

    internal data class EntityWithJoins(
        @Column(nullable = false)
        val firstName: String = "",

        @OneToMany
        val myJoin: OtherEntity? = null,
    ) {
        inner class OtherEntity(
            @Column(nullable = false)
            val id: String = "",
        )
    }

    internal data class EntityWithMultipleJoins(
        @Column(nullable = false)
        val firstName: String = "",

        @OneToMany
        val myJoin1: OtherEntity? = null,
    ) {
        inner class OtherEntity(
            @Column(nullable = false)
            val id: String = "",

            @ElementCollection
            val myJoin2: Set<String>,
        )

        inner class MyEntityJoinHandler : EntityJoinHandler {
            override fun supports(propertyInfos: PropertyInfos): Boolean {
                return propertyInfos.fieldName == "myJoin2"
            }

            override fun handle(propertyInfos: PropertyInfos): JoinInfo {
                return JoinInfo(JoinType.LEFTJOIN, true)
            }
        }
    }

    internal data class EntityWithBidirectionalJoins(
        @Column(nullable = false)
        val firstName: String = "",

        @OneToMany
        val myJoin1: MyEntity1? = null,

        @OneToMany
        val myJoin2: MyEntity2? = null,
    ) {
        inner class MyEntity1(
            @Column(nullable = false)
            val id: String = "",

            @ManyToOne
            val parent: EntityWithBidirectionalJoins? = null,
        )

        inner class MyEntity2(
            @Column(nullable = false)
            val id: String = "",

            @OneToOne
            val myEntity1: MyEntity1? = null,
        )
    }

}