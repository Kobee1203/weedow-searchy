package com.weedow.spring.data.search.query.querytype

import com.querydsl.core.types.PathType
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class QEntityAliasImplTest {

    @Test
    fun get() {
        val qEntityAlias = QEntityAliasImpl(Any::class.java, "myfield")

        assertThat(qEntityAlias.metadata.pathType).isEqualTo(PathType.VARIABLE)
        assertThat(qEntityAlias.metadata.element).isEqualTo("myfield")
        assertThat(qEntityAlias.metadata.name).isEqualTo("myfield")
        assertThat(qEntityAlias.metadata.isRoot).isEqualTo(true)
        assertThat(qEntityAlias.metadata.parent).isNull()
        assertThat(qEntityAlias.metadata.rootPath).isNull()

        Assertions.assertThatThrownBy { qEntityAlias.get("anyString") }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessage("QEntityAlias does not support get(String) method")
    }
}