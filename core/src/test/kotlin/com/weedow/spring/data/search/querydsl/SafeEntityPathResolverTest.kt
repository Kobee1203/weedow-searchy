package com.weedow.spring.data.search.querydsl

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.PathType
import com.querydsl.core.types.dsl.PathBuilder
import com.weedow.spring.data.search.common.model.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SafeEntityPathResolverTest {

    @Test
    fun create_path() {
        val entityPathResolver = SafeEntityPathResolver("")

        val entityPath = entityPathResolver.createPath(Person::class.java)

        assertThat(entityPath).isInstanceOf(PathBuilder::class.java)

        assertThat(entityPath.type).isEqualTo(Person::class.java)
        assertThat(entityPath.root).isEqualTo(ExpressionUtils.path(Person::class.java, PathMetadataFactory.forVariable("person")))
        assertThat(entityPath.annotatedElement).isEqualTo(Person::class.java)
        assertThat(entityPath.metadata.pathType).isEqualTo(PathType.VARIABLE)
        assertThat(entityPath.metadata.element).isEqualTo("person")
        assertThat(entityPath.metadata.name).isEqualTo("person")
        assertThat(entityPath.metadata.isRoot).isEqualTo(true)
        assertThat(entityPath.metadata.parent).isNull()
        assertThat(entityPath.metadata.rootPath).isNull()
    }

    @Test
    fun create_path_with_query_class() {
        val entityPathResolver = SafeEntityPathResolver("")

        val entityPath = entityPathResolver.createPath(MyEntity::class.java)

        assertThat(entityPath).isInstanceOf(QMyEntity::class.java)

        assertThat(entityPath.type).isEqualTo(MyEntity::class.java)
        assertThat(entityPath.root).isEqualTo(ExpressionUtils.path(MyEntity::class.java, PathMetadataFactory.forVariable("myEntity")))
        assertThat(entityPath.annotatedElement).isEqualTo(MyEntity::class.java)
        assertThat(entityPath.metadata.pathType).isEqualTo(PathType.VARIABLE)
        assertThat(entityPath.metadata.element).isEqualTo("myEntity")
        assertThat(entityPath.metadata.name).isEqualTo("myEntity")
        assertThat(entityPath.metadata.isRoot).isEqualTo(true)
        assertThat(entityPath.metadata.parent).isNull()
        assertThat(entityPath.metadata.rootPath).isNull()
    }

    @Test
    fun create_path_with_query_class_and_suffix() {
        val entityPathResolver = SafeEntityPathResolver(".suffix")

        val entityPath = entityPathResolver.createPath(MyEntity::class.java)

        assertThat(entityPath).isInstanceOf(com.weedow.spring.data.search.querydsl.suffix.QMyEntity::class.java)

        assertThat(entityPath.type).isEqualTo(MyEntity::class.java)
        assertThat(entityPath.root).isEqualTo(ExpressionUtils.path(MyEntity::class.java, PathMetadataFactory.forVariable("myEntity")))
        assertThat(entityPath.annotatedElement).isEqualTo(MyEntity::class.java)
        assertThat(entityPath.metadata.pathType).isEqualTo(PathType.VARIABLE)
        assertThat(entityPath.metadata.element).isEqualTo("myEntity")
        assertThat(entityPath.metadata.name).isEqualTo("myEntity")
        assertThat(entityPath.metadata.isRoot).isEqualTo(true)
        assertThat(entityPath.metadata.parent).isNull()
        assertThat(entityPath.metadata.rootPath).isNull()
    }

}