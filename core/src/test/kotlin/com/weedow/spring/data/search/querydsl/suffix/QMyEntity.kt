package com.weedow.spring.data.search.querydsl.suffix

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathInits
import com.weedow.spring.data.search.querydsl.MyEntity

class QMyEntity(
    type: Class<out MyEntity>,
    metadata: PathMetadata,
    inits: PathInits
) : EntityPathBase<MyEntity>(type, metadata, inits) {

    constructor(variable: String) : this(MyEntity::class.java, PathMetadataFactory.forVariable(variable), INITS)
    constructor(path: Path<out MyEntity>) : this(path.type, path.metadata, PathInits.getFor(path.metadata, INITS))
    constructor(metadata: PathMetadata) : this(MyEntity::class.java, metadata, PathInits.getFor(metadata, INITS))
    constructor(metadata: PathMetadata, inits: PathInits) : this(MyEntity::class.java, metadata, inits)

    companion object {

        private val INITS = PathInits.DIRECT2

        @JvmField
        val myEntityClass = QMyEntity("myEntity")
    }

}
