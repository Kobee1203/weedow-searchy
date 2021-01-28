package com.weedow.searchy.query.querytype

import com.querydsl.core.types.Path
import com.querydsl.core.types.PathMetadata
import com.querydsl.core.types.PathMetadataFactory
import com.querydsl.core.types.dsl.ArrayPath
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathInits
import com.weedow.searchy.context.SearchyContext
import com.weedow.searchy.query.querytype.QEntityImpl.Companion.INITS

/**
 * [QEntity] implementation.
 *
 * @param searchyContext [SearchyContext]
 * @param entityClass Entity Class
 * @param pathMetadata [PathMetadata]
 * @param inits [PathInits] that defines path initializations that can be attached to properties. Default is [INITS].
 */
open class QEntityImpl<T>(
    private val searchyContext: SearchyContext,
    entityClass: Class<T>,
    pathMetadata: PathMetadata,
    private val inits: PathInits = INITS
) : EntityPathBase<T>(entityClass, pathMetadata, inits), QEntity<T> {

    constructor(
        searchyContext: SearchyContext,
        entityClass: Class<T>,
        variable: String,
        inits: PathInits = INITS,
    ) : this(searchyContext, entityClass, PathMetadataFactory.forVariable(variable), inits)

    companion object {
        /** Default value of [PathInits]. Prevents infinite loop initialization. */
        private val INITS = PathInits.DIRECT2
    }

    private val fieldPaths = mutableMapOf<String, QPath<*>>()

    init {
        searchyContext.getAllPropertyInfos(type).forEach { propertyInfos ->
            addField(propertyInfos)
        }
    }

    override fun get(fieldName: String): QPath<*> {
        return fieldPaths.getOrElse(fieldName) {
            throw IllegalArgumentException("Could not found the Path related to the given field name '$fieldName'")
        }
    }

    private fun addField(propertyInfos: PropertyInfos) {
        val path: Path<*>? = when (propertyInfos.elementType) {
            ElementType.BOOLEAN -> createBoolean(propertyInfos.fieldName)
            ElementType.STRING -> createString(propertyInfos.fieldName)
            ElementType.NUMBER -> createNumber(propertyInfos.fieldName, propertyInfos.type)
            ElementType.DATE -> createDate(propertyInfos.fieldName, propertyInfos.type)
            ElementType.DATETIME -> createDateTime(propertyInfos.fieldName, propertyInfos.type)
            ElementType.TIME -> createTime(propertyInfos.fieldName, propertyInfos.type)
            ElementType.ENUM -> @Suppress("UNCHECKED_CAST") createEnum(propertyInfos.fieldName, propertyInfos.type as Class<Enum<*>>)
            ElementType.ARRAY -> createArray(propertyInfos.fieldName, propertyInfos.type, propertyInfos.parameterizedTypes[0])
            ElementType.LIST -> createList(propertyInfos.fieldName, propertyInfos.parameterizedTypes[0], propertyInfos.queryType, PathInits.DIRECT2)
            ElementType.SET -> createSet(propertyInfos.fieldName, propertyInfos.parameterizedTypes[0], propertyInfos.queryType, PathInits.DIRECT2)
            ElementType.COLLECTION -> createCollection(
                propertyInfos.fieldName,
                propertyInfos.parameterizedTypes[0],
                propertyInfos.queryType,
                PathInits.DIRECT2
            )
            ElementType.MAP -> createMap(
                propertyInfos.fieldName,
                propertyInfos.parameterizedTypes[0],
                propertyInfos.parameterizedTypes[1],
                propertyInfos.queryType
            )
            ElementType.ENTITY ->
                if (inits.isInitialized(propertyInfos.fieldName))
                    QEntityImpl(searchyContext, propertyInfos.type, forProperty(propertyInfos.fieldName), inits.get(propertyInfos.fieldName))
                else null
            ElementType.COMPARABLE -> createComparable(propertyInfos.fieldName, propertyInfos.type)
            else -> {
                if (searchyContext.isUnknownAsEmbedded) {
                    if (inits.isInitialized(propertyInfos.fieldName))
                        QEntityImpl(searchyContext, propertyInfos.type, forProperty(propertyInfos.fieldName), inits.get(propertyInfos.fieldName))
                    else null
                } else {
                    createSimple(propertyInfos.fieldName, propertyInfos.type)
                }
            }
        }
        if (path != null) {
            fieldPaths[propertyInfos.fieldName] = QPathImpl(path, propertyInfos)
        }
    }

    private fun <A, E> createArray(fieldName: String, type: Class<A>, @Suppress("UNUSED_PARAMETER") componentType: Class<E>): ArrayPath<A, E> {
        return createArray(fieldName, type)
    }

}